package ru.protei.portal.core.client.youtrack.rest;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiClient;
import ru.protei.portal.core.model.yt.ChangeResponse;

/**
 * @deprecated Переход на {@link YoutrackApiClient}
 */
@Deprecated
public interface YoutrackRestClient {

    /**
     * @deprecated Переход на {@link YoutrackApiClient}
     */
    @Deprecated
    Result<ChangeResponse> getIssueChanges( String issueId );
}
