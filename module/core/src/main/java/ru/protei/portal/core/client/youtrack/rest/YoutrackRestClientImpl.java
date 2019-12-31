package ru.protei.portal.core.client.youtrack.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.model.dict.En_ResultStatus;
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

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_ResultStatus.NOT_CREATED;

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

    @Override
    public Result<List<YtAttachment>> getIssueAttachments( String issueId ) {
        return error(En_ResultStatus.NOT_AVAILABLE);
//        return client.read( getBaseUrl() + "/issue/" + issueId + "/attachment", AttachmentResponse.class )
//                .map( ar -> ar.getAttachments() );
    }

    @Override
    public Result<String> createIssue( String project, String summary, String description ) {
        String uri = UriComponentsBuilder.fromHttpUrl( getBaseUrl() + "/issue" )
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
    public Result<List<Issue>> getIssuesByProjectAndUpdated( String projectId, Date updatedAfter ) {
        return error(En_ResultStatus.NOT_AVAILABLE);
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl( getBaseUrl() + "/issue/byproject/" + projectId )
//                .queryParam( "with", "id" )
//                .queryParam( "max", MAX_ISSUES_IN_RESPONSE );
//
//        if (updatedAfter != null)
//            builder.queryParam( "updatedAfter", updatedAfter.getTime() );
//
//        String uri = builder.build()
//                .encode()
//                .toUriString();
//
//        return client.read( uri, Issue[].class ).map( Arrays::asList );
    }

    @Deprecated
    @Override
    public Result<Issue> getIssue( String issueId ) {
        return error(En_ResultStatus.NOT_AVAILABLE);
//        return client.read( getBaseUrl() + "/issue/" + issueId, Issue.class );
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

