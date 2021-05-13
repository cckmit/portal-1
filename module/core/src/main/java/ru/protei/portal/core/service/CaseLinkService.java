package ru.protei.portal.core.service;

import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;

import java.util.List;
import java.util.Map;

public interface CaseLinkService {

    Result<Map<En_CaseLink, String>> getLinkMap();

    // TODO: линки используются на уровне Анкет и обращений. Для проверки привилегий нужна более гибкая проверка
    @Privileged(requireAny = { En_Privilege.ISSUE_VIEW, En_Privilege.PROJECT_VIEW, En_Privilege.EMPLOYEE_REGISTRATION_VIEW })
    Result<List<CaseLink>> getLinks(AuthToken token, Long caseId);

    Result<YouTrackIssueInfo> getYoutrackIssueInfo(AuthToken authToken, String ytId);

    @Privileged(requireAny = { En_Privilege.ISSUE_EDIT, En_Privilege.PROJECT_EDIT })
    @Auditable(En_AuditType.LINK_CREATE)
    Result<List<CaseLink>> createLinks(AuthToken authToken, List<CaseLink> links, En_CaseType caseType);

    @Privileged(requireAny = { En_Privilege.ISSUE_EDIT, En_Privilege.PROJECT_EDIT })
    @Auditable(En_AuditType.LINK_CREATE)
    Result<CaseLink> createLinkWithPublish(AuthToken authToken, CaseLink value, En_CaseType caseType);

    @Privileged(requireAny = { En_Privilege.ISSUE_EDIT, En_Privilege.PROJECT_EDIT })
    @Auditable(En_AuditType.LINK_REMOVE)
    Result deleteLinks(AuthToken token, List<CaseLink> links);

    @Privileged(requireAny = { En_Privilege.ISSUE_EDIT, En_Privilege.PROJECT_EDIT })
    @Auditable(En_AuditType.LINK_REMOVE)
    Result<CaseLink> deleteLinkWithPublish(AuthToken authToken, Long id, En_CaseType caseType);

    Result<String> setYoutrackIdToCaseNumbers(AuthToken token, String youtrackId, List<Long> caseNumberList);

    Result<String> setYoutrackIdToProjectNumbers(AuthToken token, String youtrackId, List<Long> projectNumberList);

    Result<String> changeYoutrackId(AuthToken token, String oldYoutrackId, String newYoutrackId);

    Result<List<Long>> getProjectIdsByYoutrackId(AuthToken token, String youtrackId);

    Result<CaseLink> getYtLink(AuthToken token, String youtrackId, Long caseId);
    Result<UitsIssueInfo> getUitsIssueInfo(AuthToken authToken, Long uitsId);
}
