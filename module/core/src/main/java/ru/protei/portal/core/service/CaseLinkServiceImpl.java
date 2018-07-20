package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.service.user.AuthService;

import java.util.*;

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

        mergeCaseLinksByScope(isShowOnlyPrivate, case_id, caseLinks);

        List<CaseLink> caseLinksOld = caseLinkDAO.getCaseLinks(new CaseLinkQuery(case_id, isShowOnlyPrivate));

        List<Long> idsToRemove = getLinkIdsToRemove(caseLinks, caseLinksOld);

        createCrossCRMLinks(caseLinks, caseLinksOld);

        for (CaseLink caseLink : caseLinks) {
            caseLinkDAO.saveOrUpdate(caseLink);
        }
        caseLinkDAO.removeByKeys(idsToRemove);

        return new CoreResponse<>().success(null);
    }

    private void mergeCaseLinksByScope(boolean isShowOnlyPrivate, long case_id, List<CaseLink> updated) {
        if (updated == null) {
            updated = new ArrayList<>();
        }

        // если права ограничены, отфильтровываем updated чтобы не проскочили ссылки без права добавления/удаления
        applyCaseLinksByScope(isShowOnlyPrivate, updated);

        // если права ограничены, дополняем updated "невидимыми" ссылками
        if (isShowOnlyPrivate) {
            Set<CaseLink> current = new HashSet<>(caseLinkDAO.getCaseLinks(new CaseLinkQuery(case_id, false)));
            for (CaseLink caseLink : current) {
                if (caseLink.getType().isForcePrivacy() || (
                        En_CaseLink.CRM.equals(caseLink.getType()) &&
                        ifCaseObjectPrivate(caseLink)
                )) {
                    updated.add(caseLink);
                }
            }
        }
    }

    private void applyCaseLinksByScope(boolean isShowOnlyPrivate, List<CaseLink> caseLinks) {
        if (CollectionUtils.isEmpty(caseLinks)) {
            return;
        }
        if (isShowOnlyPrivate) {
            caseLinks.removeIf(caseLink ->
                    caseLink.getType().isForcePrivacy() || (
                            En_CaseLink.CRM.equals(caseLink.getType()) &&
                            ifCaseObjectPrivate(caseLink)
                    )
            );
        }
    }

    private boolean ifCaseObjectPrivate(CaseLink caseLink) {
        if (caseLink.isPrivateCase() != null && caseLink.isPrivateCase()) {
            return true;
        }
        try {
            CaseObject co = caseObjectDAO.getCaseByCaseno(Long.parseLong(caseLink.getRemoteId()));
            if (co != null && co.isPrivateCase()) {
                return true;
            }
        } catch (NumberFormatException e) {
            // remote id is not a long value
        }
        return false;
    }

    private List<Long> getLinkIdsToRemove(List<CaseLink> caseLinks, List<CaseLink> caseLinksOld) {
        List<Long> idsToRemove = new ArrayList<>();
        outer: for (CaseLink caseLinkOld : caseLinksOld) {
            for (CaseLink caseLink : caseLinks) {
                if (caseLinkOld.getId().equals(caseLink.getId())) {
                    // contains
                    continue outer;
                }
            }
            idsToRemove.add(caseLinkOld.getId());
        }
        return idsToRemove;
    }

    private void createCrossCRMLinks(List<CaseLink> caseLinks, List<CaseLink> caseLinksOld) {
        outer: for (ListIterator<CaseLink> it = caseLinks.listIterator(); it.hasNext();) {
            CaseLink caseLink = it.next();
            for (CaseLink caseLinkOld : caseLinksOld) {
                if (caseLinkOld.equals(caseLink)) {
                    continue outer;
                }
            }
            // у нас тут оказывается новая ссылка
            Long caseId = caseLink.getCaseId();
            Long caseIdRemote = getCaseIdFromLinkRemoteId(caseLink);
            if (caseIdRemote == null) {
                continue;
            }
            // да еще и ссылается на crm обращение
            // проверим, может перекрестная ссылка уже существует
            List<CaseLink> crossCaseLinks = caseLinkDAO.getCaseLinks(new CaseLinkQuery(caseIdRemote, false));
            if (crossCaseLinks != null) {
                for (CaseLink ccl : crossCaseLinks) {
                    Long cclCaseId = ccl.getCaseId();
                    Long cclCaseIdRemote = getCaseIdFromLinkRemoteId(ccl);
                    if (cclCaseIdRemote == null) {
                        continue;
                    }
                    if (caseId.equals(cclCaseIdRemote) && caseIdRemote.equals(cclCaseId)) {
                        // да, существует
                        continue outer;
                    }
                }
            }
            // неа, создаем перекрестную ссылку на CRM обращение
            Long caseNo = getCaseNoFromLinkCaseId(caseLink);
            if (caseNo == null) {
                continue;
            }
            CaseLink cl = new CaseLink();
            cl.setCaseId(caseIdRemote);
            cl.setType(En_CaseLink.CRM);
            cl.setRemoteId(String.valueOf(caseNo));
            it.add(cl);
        }
    }

    private Long getCaseIdFromLinkRemoteId(CaseLink caseLink) {
        if (!En_CaseLink.CRM.equals(caseLink.getType())) {
            return null;
        }
        try {
            return caseObjectDAO.getCaseId(En_CaseType.CRM_SUPPORT, Long.parseLong(caseLink.getRemoteId()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long getCaseNoFromLinkCaseId(CaseLink caseLink) {
        if (!En_CaseLink.CRM.equals(caseLink.getType())) {
            return null;
        }
        try {
            return caseObjectDAO.getCaseNo(caseLink.getCaseId());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
