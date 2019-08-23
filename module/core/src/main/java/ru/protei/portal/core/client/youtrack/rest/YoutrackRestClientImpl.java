package ru.protei.portal.core.client.youtrack.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.AttachmentResponse;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;
import ru.protei.portal.core.model.yt.fields.YtFields;
import ru.protei.portal.util.UriUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.CoreResponse.error;
import static ru.protei.portal.api.struct.CoreResponse.ok;
import static ru.protei.portal.core.model.dict.En_ResultStatus.NOT_CREATED;

/**
 * Created by admin on 15/11/2017.
 */
public class YoutrackRestClientImpl implements YoutrackRestClient {

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        BASE_URL = portalConfig.data().youtrack().getApiBaseUrl() + "/rest";
    }

    @Override
    public CoreResponse<ChangeResponse> getIssueChanges( String issueId ) {
        return client.read( BASE_URL + "/issue/" + issueId + "/changes", ChangeResponse.class );
    }

    @Override
    public CoreResponse<List<YtAttachment>> getIssueAttachments( String issueId ) {
        return client.read( BASE_URL + "/issue/" + issueId + "/attachment", AttachmentResponse.class )
                .map( ar -> ar.getAttachments() );
    }

    @Override
    public CoreResponse<String> createIssue( String project, String summary, String description ) {
        String uri = UriComponentsBuilder.fromHttpUrl( BASE_URL + "/issue" )
                .queryParam( "project", project )
                .queryParam( "summary", summary )
                .queryParam( "description", StringUtils.emptyIfNull( description ) )
                .build()
                .encode()
                .toUriString();

        return client.execute((template, headers) ->
                template.exchange( uri, HttpMethod.PUT, new HttpEntity<>( headers ), String.class ) ).flatMap( response -> {
            String issueId = UriUtils.getLastPathSegment( response.getHeaders().getLocation() );
            if (issueId == null) {
                log.error( "failed to create issue: failed to extract issue id from response Location header: {}", response.getHeaders().getLocation() );
                error( NOT_CREATED );
            }
            log.debug("created issue with id = {}", issueId);
            return ok( issueId );
        } );
    }

    @Override
    public CoreResponse<List<Issue>> getIssuesByProjectAndUpdated( String projectId, Date updatedAfter ) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl( BASE_URL + "/issue/byproject/" + projectId )
                .queryParam( "with", "id" )
                .queryParam( "max", MAX_ISSUES_IN_RESPONSE );

        if (updatedAfter != null)
            builder.queryParam( "updatedAfter", updatedAfter.getTime() );

        String uri = builder.build()
                .encode()
                .toUriString();

        return client.read( uri, Issue[].class ).map( Arrays::asList );
    }

    @Deprecated
    @Override
    public CoreResponse<String> removeCrmNumber( String issueId ) {
        return client.update( makeYoutrackCommand( issueId, YtFields.crmNumber, YtFields.crmNumberEmptyValue ), String.class );
    }

    @Deprecated
    @Override
    public CoreResponse<String> setCrmNumber( String issueId, Long caseNumber ) {
        return client.update( makeYoutrackCommand( issueId, YtFields.crmNumber, String.valueOf( caseNumber ) ), String.class );
    }

    @Deprecated
    @Override
    public CoreResponse<Issue> getIssue( String issueId ) {
        return client.read( BASE_URL + "/issue/" + issueId, Issue.class );
    }

    private String makeYoutrackCommand( String issueId, String fieldname, String fieldValue ) {
        return UriComponentsBuilder.fromHttpUrl( BASE_URL + "/issue/" + issueId + "/execute" )
                .queryParam( "command", fieldname + " " + fieldValue )
                .build()
                .encode()
                .toUriString();
    }



    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YoutrackHttpClient client;

    private String BASE_URL;

    private final static int MAX_ISSUES_IN_RESPONSE = Integer.MAX_VALUE;
    private final static Logger log = LoggerFactory.getLogger( YoutrackRestClientImpl.class );
}

