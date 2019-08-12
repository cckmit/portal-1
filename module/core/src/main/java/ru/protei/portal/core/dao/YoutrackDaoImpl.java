package ru.protei.portal.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.AttachmentResponse;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;
import ru.protei.portal.core.model.yt.fields.YtFields;
import ru.protei.portal.util.UriUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import static ru.protei.portal.api.struct.CoreResponse.errorSt;
import static ru.protei.portal.api.struct.CoreResponse.ok;
import static ru.protei.portal.core.model.dict.En_ResultStatus.NOT_CREATED;

/**
 * Created by admin on 15/11/2017.
 */
public class YoutrackDaoImpl implements YoutrackDAO {

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        authHeaders = new HttpHeaders();
        authHeaders.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
        authHeaders.set( "Authorization", "Bearer " + portalConfig.data().youtrack().getAuthToken() );

        BASE_URL = portalConfig.data().youtrack().getApiBaseUrl();
    }

    @Override
    public CoreResponse<ChangeResponse> getIssueChanges( String issueId ) {
        return read( BASE_URL + "/issue/" + issueId + "/changes", ChangeResponse.class );
    }

    @Override
    public CoreResponse<List<YtAttachment>> getIssueAttachments( String issueId ) {
        return read( BASE_URL + "/issue/" + issueId + "/attachment", AttachmentResponse.class )
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


        return execute( uri, String.class, HttpMethod.PUT ).flatMap( response -> {
            String issueId = UriUtils.getLastPathSegment( response.getHeaders().getLocation() );
            if (issueId == null) {
                log.error( "failed to create issue: failed to extract issue id from response Location header: {}", response.getHeaders().getLocation() );
                errorSt( NOT_CREATED );
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

        return read( uri, Issue[].class ).map( Arrays::asList );
    }

    @Override
    public CoreResponse<String> removeCrmNumber( String issueId ) {
        return update( makeYoutrackCommand( issueId, YtFields.crmNumber, YtFields.crmNumberEmptyValue ), String.class );
    }

    @Override
    public CoreResponse<String> updateCrmNumber( String issueId, Long caseNumber ) {
        return update( makeYoutrackCommand( issueId, YtFields.crmNumber, String.valueOf( caseNumber ) ), String.class );
    }

    @Override
    public CoreResponse<Issue> getIssue( String issueId ) {
        return read( BASE_URL + "/issue/" + issueId, Issue.class );
    }

    private String makeYoutrackCommand( String issueId, String fieldname, String fieldValue ) {
        return UriComponentsBuilder.fromHttpUrl( BASE_URL + "/issue/" + issueId + "/execute" )
                .queryParam( "command", fieldname + " " + fieldValue )
                .build()
                .encode()
                .toUriString();
    }

    private <T> CoreResponse<T> read( String url, Class<T> clazz ) {
        return execute( url, clazz, HttpMethod.GET ).map( ResponseEntity::getBody );
    }

    private <T> CoreResponse<T> create( String url, Class<T> clazz ) {
        return execute( url, clazz, HttpMethod.PUT ).map( ResponseEntity::getBody );
    }

    private <T> CoreResponse<T> update( String url, Class<T> clazz ) {
        return execute( url, clazz, HttpMethod.POST ).map( ResponseEntity::getBody );
    }

    private <T> CoreResponse<ResponseEntity<T>> execute( String url, Class<T> clazz, HttpMethod httpMethod ) {
        RestTemplateResponseErrorHandler errorHandler = new RestTemplateResponseErrorHandler();
        RestTemplate ytClient = makeClient( errorHandler );

        ResponseEntity<T> response;

        try {
            response = ytClient.exchange( url, httpMethod, new HttpEntity<>( authHeaders ), clazz );
        } catch (Exception e) {
            log.warn( "execute(): Can't execute youtrack request for url {} and {}, unexpected exception: {}", url, clazz, e );
            return errorSt( En_ResultStatus.GET_DATA_ERROR );
        }
        if (response == null) {
            log.warn( "execute(): Can't execute youtrack request for url {} and {}, result is null", url, clazz );
            return errorSt( En_ResultStatus.GET_DATA_ERROR );
        }
        if (HttpStatus.NOT_FOUND.equals( response.getStatusCode() )) {
            log.warn( "execute(): Can't get data from youtrack, NOT_FOUND. url {} and {}", url, clazz );
            return errorSt( En_ResultStatus.NOT_FOUND );
        }
        if (!errorHandler.isOk()) {
            if (HttpStatus.NOT_FOUND.equals( errorHandler.getStatus() )) {
                log.warn( "execute(): Can't get data from youtrack, request failed with error NOT_FOUND. url {} and {} message: {}", url, clazz, response.getBody() );
                return errorSt( En_ResultStatus.NOT_FOUND );
            }
            log.warn( "execute(): Can't execute youtrack request, request failed with status {}. url {} and {} message: {} ", errorHandler.getStatus(), url, clazz, response.getBody() );
            return errorSt( En_ResultStatus.GET_DATA_ERROR );
        }

        return ok( response );
    }

    private RestTemplate makeClient( RestTemplateResponseErrorHandler errorHandler ) {
        RestTemplate template = new RestTemplate();
        if (errorHandler != null) {
            template.setErrorHandler( errorHandler );
        }
        ((DefaultUriTemplateHandler) template.getUriTemplateHandler()).setStrictEncoding( true );
        return template;
    }

    @Autowired
    private PortalConfig portalConfig;

    private HttpHeaders authHeaders;
    private String BASE_URL;

    private final static int MAX_ISSUES_IN_RESPONSE = Integer.MAX_VALUE;
    private final static Logger log = LoggerFactory.getLogger( YoutrackDaoImpl.class );
}

class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    public boolean isOk() {
        return errorStatus == null || HttpStatus.OK.equals( errorStatus );
    }

    public HttpStatus getStatus() {
        return errorStatus;
    }

    @Override
    public boolean hasError( ClientHttpResponse httpResponse ) throws IOException {
        return (CLIENT_ERROR == httpResponse.getStatusCode().series()
                || SERVER_ERROR == httpResponse.getStatusCode().series());
    }

    @Override
    public void handleError( ClientHttpResponse httpResponse ) throws IOException {
        errorStatus = httpResponse.getStatusCode();
        log.warn( "handleError(): Youtrack http api request error. status code: {} : {}"
                , httpResponse.getStatusCode()
                , httpResponse.getStatusText()
        );
    }

    private HttpStatus errorStatus;
    private final static Logger log = LoggerFactory.getLogger( RestTemplateResponseErrorHandler.class );
}
