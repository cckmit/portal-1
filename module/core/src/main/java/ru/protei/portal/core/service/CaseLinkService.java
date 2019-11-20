package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CaseLinkService {

    Result<Map<En_CaseLink, String>> getLinkMap();

    // TODO: линки используются на уровне Анкет и обращений. Для проверки привилегий нужна более гибкая проверка
//    @Privileged({ En_Privilege.ISSUE_VIEW })
    Result<List<CaseLink>> getLinks( AuthToken token, Long caseId);

    @Privileged({ En_Privilege.ISSUE_VIEW })
    Result<List<CaseLink>> updateLinks( AuthToken token, Long caseId, Person initiator, Collection<CaseLink> caseLinks );

    Result<YouTrackIssueInfo> getYoutrackIssueInfo(AuthToken authToken, String ytId );

    Result<Long> addYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId );

    Result<Long> removeYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId );

    Result<Long> createLink(AuthToken authToken, Person initiator, CaseLink value);

    Result removeLink(AuthToken authToken, Person initiator, Long id);
}
