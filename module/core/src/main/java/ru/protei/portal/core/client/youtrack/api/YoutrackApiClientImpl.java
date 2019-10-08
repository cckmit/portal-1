package ru.protei.portal.core.client.youtrack.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.api.builder.IssueQueryBuilder;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.model.yt.api.IssueApi;
import ru.protei.portal.core.model.yt.api.issue.IssueCustomField;

import javax.annotation.PostConstruct;

import static ru.protei.portal.core.model.helper.StringUtils.join;

/**
 *  Api youtrack
 */
public class YoutrackApiClientImpl implements YoutrackApiClient {

    @Override
    public Result<String> removeCrmNumber( IssueApi issue ) {
        String url = IssueQueryBuilder.create( getBaseUrl(), issue.id ).build();
        String body = makeChangeCustomField( issue.getCrmNumberField(), null );
        return client.update( url, String.class, body );
    }

    @Override
    public Result<String> setCrmNumber( IssueApi issue, Long caseNumber ) {
        String url = IssueQueryBuilder.create( getBaseUrl(), issue.id ).build();
        String body = makeChangeCustomField( issue.getCrmNumberField(), String.valueOf( caseNumber ) );
        return client.update( url, String.class, body );
    }

    @Override
    public Result<IssueApi> getIssue( String issueId ) {
        String url = IssueQueryBuilder.create( getBaseUrl(), issueId ).preset().
                idAndCustomFieldsDefaults().build();
        return client.read( url, IssueApi.class );
    }

    private static String makeChangeCustomField( IssueCustomField customField, String value ) {
        return join( "{ \"customFields\": [ {\"id\":\"", customField.id, "\",\"$type\":\"", customField.$type, "\",\"value\":", value, "} ] }" ).toString();
    }

    private String getBaseUrl() {
        return portalConfig.data().youtrack().getApiBaseUrl() + "/api";
    }

    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YoutrackHttpClient client;


    private final static Logger log = LoggerFactory.getLogger( YoutrackApiClientImpl.class );
}

