package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;

import java.util.List;
import java.util.Map;

public interface CaseLinkService {

    CoreResponse<Map<En_CaseLink, String>> getLinkMap();

    @Privileged({ En_Privilege.ISSUE_VIEW })
    CoreResponse<List<CaseLink>> getLinks(AuthToken token, Long caseId);

    @Privileged({ En_Privilege.ISSUE_VIEW })
    CoreResponse mergeLinks(AuthToken token, Long caseId, Long caseNumber, List<CaseLink> links);

    CoreResponse<YouTrackIssueInfo> getIssueInfo( AuthToken authToken, String ytId );

    CoreResponse<List<CaseLink>> getYoutrackLinks( Long caseId);

    CoreResponse<Long> createLink( CaseLink newLink );
}
