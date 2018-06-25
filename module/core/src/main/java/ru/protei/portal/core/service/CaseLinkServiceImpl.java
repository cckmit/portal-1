package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
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

        List<CaseLink> caseLinks = caseLinkDAO.getByCaseId(case_id);

        applyCaseLinksByScope(token, caseLinks);

        return new CoreResponse<List<CaseLink>>().success(caseLinks);
    }

    @Override
    public CoreResponse mergeLinks(AuthToken token, long case_id, List<CaseLink> caseLinks) {

        for (CaseLink caseLink : caseLinks) {
            if (caseLink.getCaseId() == null) {
                caseLink.setCaseId(case_id);
            }
        }

        mergeCaseLinksByScope(token, case_id, caseLinks);

        List<Long> idsToRemove = new ArrayList<>();
        List<CaseLink> caseLinksOld = caseLinkDAO.getByCaseId(case_id);
        outer: for (CaseLink caseLinkOld : caseLinksOld) {
            for (CaseLink caseLink : caseLinks) {
                if (caseLinkOld.getId().equals(caseLink.getId())) {
                    // contains
                    continue outer;
                }
            }
            idsToRemove.add(caseLinkOld.getId());
        }

        for (CaseLink caseLink : caseLinks) {
            caseLinkDAO.saveOrUpdate(caseLink);
        }
        caseLinkDAO.removeByKeys(idsToRemove);

        return new CoreResponse<>().success(null);
    }

    private void applyCaseLinksByScope(AuthToken token, List<CaseLink> caseLinks) {
        if (CollectionUtils.isEmpty(caseLinks)) {
            return;
        }
        UserSessionDescriptor descriptor = authService.findSession(token);
        Set<UserRole> roles = descriptor.getLogin().getRoles();
        if (!policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            caseLinks.removeIf(caseLink ->
                    caseLink.getType().isForcePrivacy() || (
                            En_CaseLink.CRM.equals(caseLink.getType()) &&
                            ifCaseObjectPrivate(caseLink)
                    )
            );
        }
    }

    private void mergeCaseLinksByScope(AuthToken token, long case_id, List<CaseLink> updated) {
        if (updated == null) {
            updated = new ArrayList<>();
        }

        // если права ограничены, отфильтровываем updated чтобы не проскочили ссылки без права добавления/удаления
        applyCaseLinksByScope(token, updated);

        // если права ограничены, дополняем updated "невидимыми" ссылками
        UserSessionDescriptor descriptor = authService.findSession(token);
        Set<UserRole> roles = descriptor.getLogin().getRoles();
        if (!policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            Set<CaseLink> current = new HashSet<>(caseLinkDAO.getByCaseId(case_id));
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

    private boolean ifCaseObjectPrivate(CaseLink caseLink) {
        if (caseLink.getCaseObject() != null) {
            if (caseLink.getCaseObject().isPrivateCase()) {
                return true;
            }
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

}
