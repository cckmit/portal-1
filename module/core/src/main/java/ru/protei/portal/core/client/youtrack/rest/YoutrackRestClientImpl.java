package ru.protei.portal.core.client.youtrack.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.yt.ChangeResponse;

import static ru.protei.portal.api.struct.Result.error;

/**
 * Created by admin on 15/11/2017.
 */
@Deprecated
public class YoutrackRestClientImpl implements YoutrackRestClient {

    @Override
    public Result<ChangeResponse> getIssueChanges( String issueId ) {
        return error(En_ResultStatus.NOT_AVAILABLE);
//        return client.read( getBaseUrl() + "/issue/" + issueId + "/changes", ChangeResponse.class );
    }

    private String getBaseUrl() {
       return portalConfig.data().youtrack().getApiBaseUrl() + "/rest";
    }

    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YoutrackHttpClient client;

    private final static int MAX_ISSUES_IN_RESPONSE = Integer.MAX_VALUE;
    private final static Logger log = LoggerFactory.getLogger( YoutrackRestClientImpl.class );
}

