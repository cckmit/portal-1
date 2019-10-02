package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.winter.core.utils.collections.DiffCollectionResult;

import java.util.List;
import java.util.Map;

public interface CaseLinkService {

    Result<Map<En_CaseLink, String>> getLinkMap();

    @Privileged({ En_Privilege.ISSUE_VIEW })
    Result<List<CaseLink>> getLinks( AuthToken token, Long caseId);

    @Privileged({ En_Privilege.ISSUE_VIEW })
    Result<DiffCollectionResult<CaseLink>> mergeLinks( AuthToken token, Long caseId, Long caseNumber, List<CaseLink> links);

    Result<YouTrackIssueInfo> getIssueInfo( AuthToken authToken, String ytId );

    Result<List<CaseLink>> getYoutrackLinks( Long caseId);

    Result<Long> addYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId );

    Result<Boolean> removeYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId );
}
