package ru.protei.portal.core.service;

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
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.AttachmentResponse;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;
import ru.protei.portal.core.model.yt.fields.YtFields;
import ru.protei.portal.util.UriUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import static ru.protei.portal.api.struct.CoreResponse.errorSt;
import static ru.protei.portal.api.struct.CoreResponse.ok;

/**
 * Created by admin on 15/11/2017.
 */
public class YoutrackServiceImpl implements YoutrackService {
    private RestTemplate ytClient;

    private HttpHeaders authHeaders;
    private String BASE_URL;

    @Autowired
    private PortalConfig portalConfig;

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        authHeaders = new HttpHeaders();
        authHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        authHeaders.set("Authorization", "Bearer " + portalConfig.data().youtrack().getAuthToken());

        ytClient = makeClient(null);

        BASE_URL = portalConfig.data().youtrack().getApiBaseUrl();
    }

    @Override
    public ChangeResponse getIssueChanges(String issue) {
        log.debug("requesting changes of {}", issue);

        ResponseEntity<ChangeResponse> resp = ytClient.exchange(
                BASE_URL + "/issue/" + issue + "/changes",
                HttpMethod.GET, new HttpEntity<>(authHeaders), ChangeResponse.class
        );
        return resp.getBody();
    }

    @Override
    public List<YtAttachment> getIssueAttachments(String issueId) {
        log.debug("requesting attachments of {}", issueId);

        ResponseEntity<AttachmentResponse> resp = ytClient.exchange(
                BASE_URL + "/issue/" + issueId + "/attachment",
                HttpMethod.GET, new HttpEntity<>(authHeaders), AttachmentResponse.class
        );
        return resp.getBody().getAttachments();
    }



    @Override
    public String createIssue(String project, String summary, String description) {
        log.info("creating issue: project={}, summary={}, description={}", project, summary, description);

        String uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/issue")
                .queryParam("project", project)
                .queryParam("summary", summary)
                .queryParam("description", StringUtils.emptyIfNull(description))
                .build()
                .encode()
                .toUriString();

        ResponseEntity<String> response = ytClient.exchange(
                uri,
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders),
                String.class
        );

        String issueId = UriUtils.getLastPathSegment(response.getHeaders().getLocation());
        if (issueId == null) {
            log.error("failed to create issue: failed to extract issue id from response Location header: {}", response.getHeaders().getLocation());
            throw new RuntimeException();
        }

        log.debug("created issue with id = {}", issueId);
        return issueId;
    }

    @Override
    public Set<String> getIssueIdsByProjectAndUpdatedAfter(String projectId, Date updatedAfter) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/issue/byproject/" + projectId)
                .queryParam("with", "id")
                .queryParam("max", MAX_ISSUES_IN_RESPONSE);

        if (updatedAfter != null)
            builder.queryParam("updatedAfter", updatedAfter.getTime());

        String uri = builder.build()
                .encode()
                .toUriString();

        ResponseEntity<Issue[]> response = ytClient.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(authHeaders), Issue[].class
        );
        return Arrays.stream(response.getBody())
                .map(Issue::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public CoreResponse<YouTrackIssueInfo> getIssueInfo( String issueId ) {
        if (issueId == null ) {
            log.warn( "getIssueInfo(): Can't get issue info. Argument issueId is mandatory" );
            return errorSt( En_ResultStatus.INCORRECT_PARAMS );
        }

        return read( BASE_URL + "/issue/" + issueId, Issue.class )
                .map( this::convertToInfo );
    }

    @Override
    public CoreResponse<String> compareAndSetIssueCrmNumber( String issueId, Long caseNumber ) {
        if (issueId == null || caseNumber == null) {
            log.warn( "setIssueCrmNumber(): Can't set youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", issueId, caseNumber );
            return errorSt( En_ResultStatus.INCORRECT_PARAMS );
        }

        return read( BASE_URL + "/issue/" + issueId, Issue.class )
                .flatMap( issue -> updateCrmNumberIfAnother( issueId, issue.getCrmNumber(), caseNumber ) );
    }

    @Override
    public CoreResponse<String> compareAndUpdateIssueCrmNumber( String issueId, Long caseNumber ) {
        if (issueId == null || caseNumber == null) {
            log.warn( "updateIssueCrmNumber(): Can't update youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", issueId, caseNumber );
            return errorSt( En_ResultStatus.INCORRECT_PARAMS );
        }

        return read( BASE_URL + "/issue/" + issueId, Issue.class )
                .flatMap( issue -> updateCrmNumberIfAnother( issueId, issue.getCrmNumber(), caseNumber ) );
    }


    public static void main(String[] args) {
        YoutrackServiceImpl youtrackService = new YoutrackServiceImpl();
        youtrackService.setIssueCrmNumber("PG-208", 100451L);
    }
    public void setIssueCrmNumber( String issueId, Long caseNumber ) {
        authHeaders = new HttpHeaders();
        authHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        authHeaders.set("Authorization", "Bearer " + "perm:ZWZyZW1vdg==.cG9ydGFsLXRlc3Q=.7oXhUOe9mxT3Khs9lnCkm8vy3tpKqn");
        BASE_URL = "https://youtrack.protei.ru/rest";

//        Issue data = read( BASE_URL + "/issue/" + issueId, Issue.class ).getData();
        compareAndRemoveIssueCrmNumber( issueId,  caseNumber);
        int stop = 0;
    }


    @Override
    public CoreResponse<String> compareAndRemoveIssueCrmNumber( String issueId, Long caseNumber ) {
        if (issueId == null) {
            log.warn( "removeIssueCrmNumber(): Can't remove youtrack issue crm number. Argument issueId is mandatory" );
            return errorSt( En_ResultStatus.INCORRECT_PARAMS );
        }

        return read( BASE_URL + "/issue/" + issueId, Issue.class )
                .flatMap( issue -> removeCrmNumberIfSame( issueId, issue.getCrmNumber(), caseNumber ) );
    }

    private CoreResponse<String> removeCrmNumberIfSame( String issueId, Long crmNumber, Long caseNumber ) {
        if (Objects.equals( crmNumber, caseNumber )) {
            return   update( makeYoutrackCommand( issueId, YtFields.crmNumber, String.valueOf( crmNumber )), String.class );
        }
        return ok();
    }

        private CoreResponse<String> updateCrmNumberIfAnother( String issueId, Long crmNumber, Long caseNumber ) {
        if (Objects.equals( crmNumber, caseNumber )) {
            return ok();
        }

        String uri = makeYoutrackCommand( issueId, YtFields.crmNumber, String.valueOf( caseNumber ) );
        return update( uri, String.class );
    }

    private String makeYoutrackCommand( String issueId, String fieldname, String fieldValue ) {
        return UriComponentsBuilder.fromHttpUrl( BASE_URL + "/issue/" + issueId + "/execute" )
                .queryParam( "command", fieldname +  " " + fieldValue )
                .build()
                .encode()
                .toUriString();
    }

    private <T> CoreResponse<T> read( String url, Class<T> clazz ) {
       return execute( url, clazz, HttpMethod.GET);
    }

    private <T> CoreResponse<T> update( String url, Class<T> clazz ) {
       return execute( url, clazz, HttpMethod.POST);
    }

    private <T> CoreResponse<T> execute( String url, Class<T> clazz, HttpMethod httpMethod ) {
        RestTemplateResponseErrorHandler errorHandler = new RestTemplateResponseErrorHandler();
        RestTemplate ytClient = makeClient( errorHandler );

        ResponseEntity<T> result;

        try {
            result = ytClient.exchange( url, httpMethod, new HttpEntity<>( authHeaders ), clazz );
        } catch (Exception e) {
            log.warn( "execute(): Can't execute youtrack request for url {} and {}, unexpected exception: {}", url, clazz, e );
            return errorSt( En_ResultStatus.GET_DATA_ERROR );
        }
        if (result == null) {
            log.warn( "execute(): Can't execute youtrack request for url {} and {}, result is null", url, clazz );
            return errorSt( En_ResultStatus.GET_DATA_ERROR );
        }
        if (HttpStatus.NOT_FOUND.equals( result.getStatusCode() )) {
            log.warn( "execute(): Can't get data from youtrack, NOT_FOUND. url {} and {}", url, clazz );
            return errorSt( En_ResultStatus.NOT_FOUND );
        }
        if (!errorHandler.isOk()) {
            if (HttpStatus.NOT_FOUND.equals( errorHandler.getStatus() )) {
                log.warn( "execute(): Can't get data from youtrack, request failed with error NOT_FOUND. url {} and {}", url, clazz );
                return errorSt( En_ResultStatus.NOT_FOUND );
            }
            log.warn( "execute(): Can't execute youtrack request, request failed with status {}. url {} and {}", errorHandler.getStatus(), url, clazz );
            return errorSt( En_ResultStatus.GET_DATA_ERROR );
        }

        return ok( result.getBody() );
    }

    private YouTrackIssueInfo convertToInfo( Issue issue ) {
        if (issue == null) return null;
        YouTrackIssueInfo issueInfo = new YouTrackIssueInfo();
        issueInfo.setId( issue.getId() );
        issueInfo.setSummary( issue.getSummary() );
        issueInfo.setDescription( issue.getDescription() );
        issueInfo.setState( EmployeeRegistrationYoutrackSynchronizer.toCaseState( issue.getStateId() ) );
        issueInfo.setImportance( toCaseImportance( issue.getPriority() ) );
        return issueInfo;
    }

    private En_ImportanceLevel toCaseImportance( String ytpriority ) {
        En_ImportanceLevel result = null;

        if (ytpriority != null) {
            switch (ytpriority) {
                case "Show-stopper":
                case "Critical":
                    result = En_ImportanceLevel.CRITICAL;
                    break;
                case "Important":
                    result = En_ImportanceLevel.IMPORTANT;
                    break;
                case "Basic":
                    result = En_ImportanceLevel.BASIC;
                    break;
                case "Low":
                    result = En_ImportanceLevel.COSMETIC;
                    break;
                default:
                    return result = null;
            }

            if (result == null) {
                log.warn( "toCaseImportance(): Detected unknown YouTrack priority level= {}", ytpriority );
            }
        }
        return result;
    }

    private RestTemplate makeClient( RestTemplateResponseErrorHandler errorHandler) {
        RestTemplate template = new RestTemplate();
        if (errorHandler != null) {
            template.setErrorHandler( errorHandler );
        }
        ((DefaultUriTemplateHandler)template.getUriTemplateHandler()).setStrictEncoding( true );
        return template;
    }


    private final static Logger log = LoggerFactory.getLogger(YoutrackServiceImpl.class);
    private final static int MAX_ISSUES_IN_RESPONSE = Integer.MAX_VALUE;
}

class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    public boolean isOk(){
        return errorStatus ==null ||  HttpStatus.OK.equals( errorStatus );
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
    }

    private HttpStatus errorStatus;
}
