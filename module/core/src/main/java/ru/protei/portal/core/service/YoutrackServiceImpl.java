package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.*;
import ru.protei.portal.util.UriUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static protei.utils.common.DateUtils.formatDate;

/**
 * Created by admin on 15/11/2017.
 */
public class YoutrackServiceImpl implements YoutrackService {
    private RestTemplate ytClient = new RestTemplate();

    private HttpHeaders authHeaders;
    private String BASE_URL;

    @Autowired
    private PortalConfig portalConfig;

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        authHeaders = new HttpHeaders();
        authHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        authHeaders.set("Authorization", "Bearer " + portalConfig.data().youtrack().getAuthToken());

        BASE_URL = portalConfig.data().youtrack().getApiBaseUrl();
    }


    @Override
    public Issue getIssueDetails(String issueId) {
        log.debug("requesting {} details", issueId);

        ResponseEntity<Issue> resp = ytClient.exchange(
                BASE_URL + "/issue/" + issueId,
                HttpMethod.GET, new HttpEntity<>(authHeaders), Issue.class
        );
        return resp.getBody();
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
    public WorkItem[] getIssueWorkItems(String issue) {
        log.debug("requesting work items of {}", issue);

        ResponseEntity<WorkItem[]> resp = ytClient.exchange(
                BASE_URL + "/issue/" + issue + "/timetracking/workitem/",
                HttpMethod.GET, new HttpEntity<>(authHeaders), WorkItem[].class
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
                .toUriString();

        ResponseEntity<String> response = ytClient.exchange(
                uri,
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders),
                String.class
        );

        String issueId = UriUtils.getLastPathSegment(response.getHeaders().getLocation());
        if (issueId == null) {
            log.error("failed to create issue: failed to extract issue id from response Location header: {}" + response.getHeaders().getLocation());
            throw new RuntimeException();
        }

        log.debug("created issue with id = {}", issueId);
        return issueId;
    }

    @Override
    public Set<String> getIssueIdsByProjectAndUpdatedAt(String projectId, Date updated) {

        String filterQuery = updated == null ? "" : "updated:" + formatDate(updated);

        String uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/issue/byproject/" + projectId)
                .queryParam("filter", filterQuery)
                .queryParam("with", "id")
                .queryParam("max", MAX_ISSUES_IN_RESPONSE)
                .build()
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
    public Set<String> getIssueIdsByProject(String projectId) {
        return getIssueIdsByProjectAndUpdatedAfter(projectId, null);
    }

    private final static Logger log = LoggerFactory.getLogger(YoutrackServiceImpl.class);
    private final static int MAX_ISSUES_IN_RESPONSE = Integer.MAX_VALUE;
}
