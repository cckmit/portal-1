package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.NumberUtils;
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
import java.util.Set;
import java.util.stream.Collectors;

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

        ytClient = new RestTemplate();
        ((DefaultUriTemplateHandler)ytClient.getUriTemplateHandler()).setStrictEncoding( true );

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
    public YouTrackIssueInfo getIssueInfo( String issueId ) {
        log.debug("getIssueInfo issueId={}", issueId);

        ResponseEntity<Issue> resp = ytClient.exchange(
                BASE_URL + "/issue/" + issueId, HttpMethod.GET,
                new HttpEntity<>( authHeaders ), Issue.class
        );

        return map(resp.getBody());
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

    private YouTrackIssueInfo map( Issue issue ) {
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


    private final static Logger log = LoggerFactory.getLogger(YoutrackServiceImpl.class);
    private final static int MAX_ISSUES_IN_RESPONSE = Integer.MAX_VALUE;
}
