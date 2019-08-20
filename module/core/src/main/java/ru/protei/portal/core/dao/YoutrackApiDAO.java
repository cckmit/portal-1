package ru.protei.portal.core.dao;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.yt.Issue;

public interface YoutrackApiDAO {

//    CoreResponse<Issue> getIssue( String issueId );

    CoreResponse<String> removeCrmNumber( String issueId );

//    CoreResponse<String> setCrmNumber( String issueId, Long caseNumber );
}
