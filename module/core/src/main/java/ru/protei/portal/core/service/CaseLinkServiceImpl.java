package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.service.user.AuthService;

import java.util.*;
import java.util.stream.Collectors;

public class CaseLinkServiceImpl implements CaseLinkService {

    @Autowired
    CaseLinkDAO caseLinkDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Autowired
    PortalConfig portalConfig;

    @Override
    public CoreResponse<Map<En_CaseLink, String>> getLinkMap() {
        Map<En_CaseLink, String> linkMap = new HashMap<>();
        linkMap.put(En_CaseLink.CRM, portalConfig.data().getCaseLinkConfig().getLinkCrm());
        linkMap.put(En_CaseLink.CRM_OLD, portalConfig.data().getCaseLinkConfig().getLinkOldCrm());
        linkMap.put(En_CaseLink.YT, portalConfig.data().getCaseLinkConfig().getLinkYouTrack());
        return new CoreResponse<Map<En_CaseLink, String>>().success(linkMap);
    }

    @Override
    public CoreResponse<List<CaseLink>> getLinks(AuthToken token, long case_id) {

        UserSessionDescriptor descriptor = authService.findSession(token);
        Set<UserRole> roles = descriptor.getLogin().getRoles();
        boolean isShowOnlyPrivate = !policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW);

        List<CaseLink> caseLinks = caseLinkDAO.getCaseLinks(new CaseLinkQuery(case_id, isShowOnlyPrivate));

        return new CoreResponse<List<CaseLink>>().success(caseLinks);
    }

    @Override
    @Transactional
    public CoreResponse mergeLinks(AuthToken token, long case_id, List<CaseLink> caseLinks) {

        if (caseLinks == null) {
            return new CoreResponse<>().success(null);
        }

        UserSessionDescriptor descriptor = authService.findSession(token);
        Set<UserRole> roles = descriptor.getLogin().getRoles();
        boolean isShowOnlyPrivate = !policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW);

        for (CaseLink caseLink : caseLinks) {
            if (caseLink.getCaseId() == null) {
                caseLink.setCaseId(case_id);
            }
        }


        caseLinks = mergeCaseLinksByScope(caseLinks, isShowOnlyPrivate, case_id);

        List<CaseLink> caseLinksOld = caseLinkDAO.getCaseLinks(new CaseLinkQuery(case_id, isShowOnlyPrivate));

        List<CaseLink> caseLinksToRemove = makeCaseLinkListToRemove(caseLinks, caseLinksOld);

        List<CaseLink> caseLinksOnlyNew = makeCaseLinkListOnlyNew(caseLinks, caseLinksOld);

        caseLinks.addAll(createCrossCRMLinks(caseLinksOnlyNew));

        caseLinksToRemove.addAll(removeCrossCRMLinks(caseLinksToRemove));


        for (CaseLink caseLink : caseLinks) {
            caseLinkDAO.saveOrUpdate(caseLink);
        }

        caseLinkDAO.removeByKeys(caseLinksToRemove.stream()
                .map(CaseLink::getId)
                .collect(Collectors.toList())
        );


        return new CoreResponse<>().success(null);
    }

    private List<CaseLink> mergeCaseLinksByScope(List<CaseLink> updated, boolean isShowOnlyPrivate, long case_id) {
        if (updated == null) {
            updated = new ArrayList<>();
        }

        // если права ограничены, отфильтровываем updated чтобы не проскочили ссылки без права добавления/удаления
        applyCaseLinksByScope(isShowOnlyPrivate, updated);

        // если права ограничены, дополняем updated "невидимыми" ссылками
        if (isShowOnlyPrivate) {
            Set<CaseLink> current = new HashSet<>(caseLinkDAO.getCaseLinks(new CaseLinkQuery(case_id, false)));
            for (CaseLink caseLink : current) {
                if (caseLink.getType().isForcePrivacy() || ifCaseLinkIsPrivate(caseLink)) {
                    updated.add(caseLink);
                }
            }
        }

        return updated;
    }

    private void applyCaseLinksByScope(boolean isShowOnlyPrivate, List<CaseLink> caseLinks) {

        if (CollectionUtils.isEmpty(caseLinks)) {
            return;
        }

        if (isShowOnlyPrivate) {
            caseLinks.removeIf(caseLink ->
                    caseLink.getType().isForcePrivacy() || ifCaseLinkIsPrivate(caseLink)
            );
        }
    }

    private List<CaseLink> makeCaseLinkListToRemove(List<CaseLink> caseLinks, List<CaseLink> caseLinksOld) {

        if (CollectionUtils.isEmpty(caseLinks) || CollectionUtils.isEmpty(caseLinksOld)) {
            return new ArrayList<>();
        }

        return caseLinksOld.stream()
                .filter(cl -> !ifContainsCaseLinkById(caseLinks, cl))
                .collect(Collectors.toList());
    }

    private boolean ifCaseLinkIsPrivate(CaseLink caseLink) {

        if (caseLink == null || !En_CaseLink.CRM.equals(caseLink.getType())) {
            return false;
        }

        if (caseLink.isPrivateCase()) {
            return true;
        }

        Long caseNo = parseRemoteIdAsCaseObjectNumber(caseLink.getRemoteId());
        if (caseNo == null) {
            return false;
        }

        CaseObject co = caseObjectDAO.getCaseByCaseno(caseNo);
        return co != null && co.isPrivateCase();
    }

    private boolean ifContainsCaseLinkById(List<CaseLink> caseLinks, CaseLink caseLink) {

        if (CollectionUtils.isEmpty(caseLinks)) {
            return false;
        }

        for (CaseLink cl : caseLinks) {
            if (Objects.equals(cl.getId(), caseLink.getId())) {
                return true;
            }
        }

        return false;
    }

    private List<CaseLink> createCrossCRMLinks(List<CaseLink> caseLinks) {

        List<CaseLink> caseLinksCRM = makeCaseLinkListOnlyCRM(caseLinks);

        if (CollectionUtils.isEmpty(caseLinksCRM)) {
            return new ArrayList<>();
        }

        List<Long> remoteCaseNumberList = makeCaseNumberList(caseLinksCRM);

        List<CaseObject> partialCaseObjects = caseObjectDAO.getCaseIdAndNumbersByCaseNumbers(remoteCaseNumberList);

        List<CaseLinkWithCaseId> caseLinksCRMWithIds = makeCaseLinkListWithIds(caseLinksCRM, partialCaseObjects);

        List<CaseLink> crossCaseCRMLinks = new ArrayList<>();

        caseLinksCRMWithIds.forEach(clWithId -> {
            CaseLink cl = createCrossCRMLink(clWithId);
            if (cl != null) {
                crossCaseCRMLinks.add(cl);
            }
        });

        return crossCaseCRMLinks;
    }

    private CaseLink createCrossCRMLink(CaseLinkWithCaseId clWithId) {

        List<CaseLink> crossCaseLinks = caseLinkDAO.getCaseLinks(new CaseLinkQuery(clWithId.remoteId, false));

        List<CaseLink> crossCaseLinksCRM = makeCaseLinkListOnlyCRM(crossCaseLinks);

        if (CollectionUtils.isEmpty(crossCaseLinksCRM)) {
            return createLink(clWithId);
        }

        List<Long> remoteCaseNumberList = makeCaseNumberList(crossCaseLinksCRM);

        List<CaseObject> partialCaseObjects = caseObjectDAO.getCaseIdAndNumbersByCaseNumbers(remoteCaseNumberList);

        List<CaseLinkWithCaseId> crossCaseLinksCRMWithIds = makeCaseLinkListWithIds(crossCaseLinksCRM, partialCaseObjects);

        for (CaseLinkWithCaseId crossClWithId : crossCaseLinksCRMWithIds) {
            if (Objects.equals(crossClWithId.remoteId, clWithId.caseLink.getCaseId())) {
                return null;
            }
        }

        return createLink(clWithId);
    }

    private CaseLink createLink(CaseLinkWithCaseId clWithId) {
        Long caseNo = caseObjectDAO.getCaseNo(clWithId.caseLink.getCaseId());
        if (caseNo == null) {
            return null;
        }
        CaseLink crossCaseLink = new CaseLink();
        crossCaseLink.setCaseId(clWithId.remoteId);
        crossCaseLink.setType(En_CaseLink.CRM);
        crossCaseLink.setRemoteId(String.valueOf(caseNo));
        return crossCaseLink;
    }

    private List<CaseLink> removeCrossCRMLinks(List<CaseLink> caseLinks) {

        List<CaseLink> caseLinksCRM = makeCaseLinkListOnlyCRM(caseLinks);

        if (CollectionUtils.isEmpty(caseLinksCRM)) {
            return new ArrayList<>();
        }

        List<Long> remoteCaseNumberList = makeCaseNumberList(caseLinksCRM);

        List<CaseObject> partialCaseObjects = caseObjectDAO.getCaseIdAndNumbersByCaseNumbers(remoteCaseNumberList);

        List<CaseLinkWithCaseId> caseLinksCRMWithIds = makeCaseLinkListWithIds(caseLinksCRM, partialCaseObjects);

        List<CaseLink> crossCaseCRMLinks = new ArrayList<>();

        caseLinksCRMWithIds.forEach(clWithId -> {
            CaseLink cl = removeCrossCRMLinks(clWithId);
            if (cl != null) {
                crossCaseCRMLinks.add(cl);
            }
        });

        return crossCaseCRMLinks;
    }

    private CaseLink removeCrossCRMLinks(CaseLinkWithCaseId clWithId) {

        List<CaseLink> crossCaseLinks = caseLinkDAO.getCaseLinks(new CaseLinkQuery(clWithId.remoteId, false));

        List<CaseLink> crossCaseLinksCRM = makeCaseLinkListOnlyCRM(crossCaseLinks);

        if (CollectionUtils.isEmpty(crossCaseLinksCRM)) {
            return null;
        }

        List<Long> remoteCaseNumberList = makeCaseNumberList(crossCaseLinksCRM);

        List<CaseObject> partialCaseObjects = caseObjectDAO.getCaseIdAndNumbersByCaseNumbers(remoteCaseNumberList);

        List<CaseLinkWithCaseId> crossCaseLinksCRMWithIds = makeCaseLinkListWithIds(crossCaseLinksCRM, partialCaseObjects);

        for (CaseLinkWithCaseId crossClWithId : crossCaseLinksCRMWithIds) {
            if (Objects.equals(crossClWithId.remoteId, clWithId.caseLink.getCaseId())) {
                return crossClWithId.caseLink;
            }
        }

        return null;
    }

    private List<CaseLink> makeCaseLinkListOnlyNew(List<CaseLink> caseLinks, List<CaseLink> caseLinksOld) {

        if (CollectionUtils.isEmpty(caseLinks)) {
            return new ArrayList<>();
        }

        return caseLinks.stream()
                .filter(cl -> !ifContainsCaseLink(caseLinksOld, cl))
                .collect(Collectors.toList());
    }

    private List<CaseLink> makeCaseLinkListOnlyCRM(List<CaseLink> caseLinks) {

        if (CollectionUtils.isEmpty(caseLinks)) {
            return new ArrayList<>();
        }

        return caseLinks.stream()
                .filter(cl -> En_CaseLink.CRM.equals(cl.getType()))
                .collect(Collectors.toList());
    }

    private List<Long> makeCaseNumberList(List<CaseLink> caseLinks) {

        if (CollectionUtils.isEmpty(caseLinks)) {
            return new ArrayList<>();
        }

        return caseLinks.stream()
                .filter(cl -> parseRemoteIdAsCaseObjectNumber(cl.getRemoteId()) != null)
                .map(cl -> parseRemoteIdAsCaseObjectNumber(cl.getRemoteId()))
                .collect(Collectors.toList());
    }

    private List<CaseLinkWithCaseId> makeCaseLinkListWithIds(List<CaseLink> caseLinks, List<CaseObject> partialCaseObjects) {

        List<CaseLinkWithCaseId> caseLinksCRMWithIds = new ArrayList<>();

        if (CollectionUtils.isEmpty(caseLinks) || CollectionUtils.isEmpty(partialCaseObjects)) {
            return caseLinksCRMWithIds;
        }

        for (CaseLink cl : caseLinks) {

            Long caseNo = parseRemoteIdAsCaseObjectNumber(cl.getRemoteId());
            if (caseNo == null) {
                continue;
            }

            for (CaseObject pco : partialCaseObjects) {
                if (Objects.equals(pco.getCaseNumber(), caseNo)) {
                    caseLinksCRMWithIds.add(new CaseLinkWithCaseId(cl, pco.getId()));
                    break;
                }
            }

        }

        return caseLinksCRMWithIds;
    }

    private boolean ifContainsCaseLink(List<CaseLink> caseLinks, CaseLink caseLink) {

        if (CollectionUtils.isEmpty(caseLinks)) {
            return false;
        }

        for (CaseLink cl : caseLinks) {
            if (cl.equals(caseLink)) {
                return true;
            }
        }

        return false;
    }

    private boolean ifCaseObjectListContainsCaseNo(List<CaseObject> caseObjects, Long caseNo) {

        if (CollectionUtils.isEmpty(caseObjects) || caseNo == null) {
            return false;
        }

        for (CaseObject caseObject : caseObjects) {
            if (Objects.equals(caseObject.getCaseNumber(), caseNo)) {
                return true;
            }
        }
        return false;
    }

    private CaseLink findCaseLinkById(List<CaseLink> caseLinks, Long caseLinkId) {

        if (CollectionUtils.isEmpty(caseLinks)) {
            return null;
        }

        for (CaseLink caseLink : caseLinks) {
            if (Objects.equals(caseLink.getId(), caseLinkId)) {
                return caseLink;
            }
        }

        return null;
    }

    private Long parseRemoteIdAsCaseObjectNumber(String remoteId) {
        try {
            return Long.parseLong(remoteId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private class CaseLinkWithCaseId {

        private CaseLinkWithCaseId(CaseLink caseLink, Long remoteId) {
            this.caseLink = caseLink;
            this.remoteId = remoteId;
        }

        private CaseLink caseLink;
        private Long remoteId;
    }
}
