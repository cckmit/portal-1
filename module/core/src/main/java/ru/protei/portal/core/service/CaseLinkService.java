package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;

import java.util.List;
import java.util.Map;

public interface CaseLinkService {

    Result<Map<En_CaseLink, String>> getLinkMap();

    // TODO: линки используются на уровне Анкет и обращений. Для проверки привилегий нужна более гибкая проверка
    @Privileged(requireAny = { En_Privilege.ISSUE_VIEW, En_Privilege.PROJECT_VIEW, En_Privilege.EMPLOYEE_REGISTRATION_VIEW })
    Result<List<CaseLink>> getLinks( AuthToken token, Long caseId);

    Result<YouTrackIssueInfo> getYoutrackIssueInfo(AuthToken authToken, String ytId );

    @Auditable(En_AuditType.LINK_CREATE)
    Result<Long> addYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId );

    @Auditable(En_AuditType.LINK_REMOVE)
    Result<Long> removeYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId );

    @Privileged(requireAny = { En_Privilege.ISSUE_EDIT, En_Privilege.PROJECT_EDIT })
    @Auditable(En_AuditType.LINK_CREATE)
    Result<Long> createLink(AuthToken authToken, CaseLink value, boolean createCrossLinks);

    @Privileged(requireAny = { En_Privilege.ISSUE_EDIT, En_Privilege.PROJECT_EDIT })
    @Auditable(En_AuditType.LINK_CREATE)
    Result<Long> createLinkWithPublish(AuthToken authToken, CaseLink value, En_CaseType caseType, boolean createCrossLinks);

    @Privileged(requireAny = { En_Privilege.ISSUE_EDIT, En_Privilege.PROJECT_EDIT })
    @Auditable(En_AuditType.LINK_REMOVE)
    Result deleteLink(AuthToken authToken, Long id);

    @Privileged(requireAny = { En_Privilege.ISSUE_EDIT, En_Privilege.PROJECT_EDIT })
    @Auditable(En_AuditType.LINK_REMOVE)
    Result deleteLinkWithPublish(AuthToken authToken, Long id, En_CaseType caseType);
}
