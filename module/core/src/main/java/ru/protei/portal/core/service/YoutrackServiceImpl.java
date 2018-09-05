package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.IssueResponse;
import ru.protei.portal.core.model.yt.WorkItem;
import ru.protei.portal.util.UriUtils;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class YoutrackServiceImpl implements YoutrackService {
    private RestTemplate ytClient = new RestTemplate();

    private HttpHeaders authHeaders;
    private String BASE_URL;

    @Autowired
    PortalConfig portalConfig;

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        authHeaders = new HttpHeaders();
        authHeaders.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
        authHeaders.set( "Authorization", "Bearer " + portalConfig.data().youtrack().getAuthToken());

        BASE_URL = portalConfig.data().youtrack().getApiBaseUrl();
    }

    @Override
    public List<Issue> getTasks(String ytLogin, String date, DateType dateType) {
        String url = BASE_URL + "/issue?max=100&with=links&with=Assignee&with=projectShortName&with=Type&with=id&with=entityId&with=Заказчик&with=Номер обращения в CRM&with=resolved&with=created&with=reporterFullName&with=reporterName";

        String filter = "";
        if ( ytLogin != null ) {
            if ( DateType.UPDATED.equals( dateType ) ) {
                filter += " обновлена кем: " + ytLogin;
            }
            else {
                filter += " исполнитель: " + ytLogin;
            }
        }

        if ( date != null ) {
            switch ( dateType ) {
                case COMPLETE: filter += " дата завершения: " + date; break;
                case CREATED: filter += " создана: " + date; break;
                case UPDATED: filter += " обновлена: " + date; break;
                case WORK_ITEMS: filter += " дата работы: " + date; break;
            }
        }

        if ( filter != null ) {
            url += URLEncoder.encode("&filter="+ filter);
        }

        String body = ytClient.exchange(
                url,
                HttpMethod.GET, new HttpEntity<>( authHeaders ), String.class
        ).getBody();

        List<Issue> issues = new ArrayList<>();
        log.info( "getTasks(): url={}", url );
        int after = 0;
        while (true) {
//        String url = "https://youtrack.protei/rest/issue?filter=обновлена кем: "+ytLogin+" обновлена: "+date+"&max=100&with=projectShortName&with=Type&with=id&with=entityId";
            ResponseEntity< IssueResponse > resp = ytClient.exchange(
                    url+"&after="+after,
                    HttpMethod.GET, new HttpEntity<>( authHeaders ), IssueResponse.class
            );
            issues.addAll( resp.getBody().getIssue() );
            log.info( "getTasks(): returned {} tasks", resp.getBody().getIssue().size() );
            if ( resp.getBody().getIssue().size() < 100 ) {
                break;
            }
            after += 100;
        }
        log.info( "getTasks(): total {} tasks", issues.size() );
        return issues;
    }

    @Override
    public Issue getIssueDetails(String issue) {
        ResponseEntity< Issue > resp = ytClient.exchange(
                BASE_URL + "/issue/"+issue+"?with=links&with=Assignee&with=projectShortName&with=Type&with=id&with=entityId&with=Заказчик&with=Номер обращения в CRM&with=resolved",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), Issue.class
        );
        return resp.getBody();
    }

    @Override
    public String getIssueChangesRaw(String issue) {
        ResponseEntity< String > resp = ytClient.exchange(
                BASE_URL + "/issue/"+issue+"/changes",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), String.class
        );
        return resp.getBody();
    }

    @Override
    public ChangeResponse getIssueChanges(String issue) {
        log.info( "requesting changes of {}", issue );

        ResponseEntity< ChangeResponse > resp = ytClient.exchange(
                BASE_URL + "/issue/"+issue+"/changes",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), ChangeResponse.class
        );
        return resp.getBody();
    }

    @Override
    public String getIssueWorkItemsRaw(String issue) {
        log.info( "requesting work items of {}", issue );

        ResponseEntity< String > resp = ytClient.exchange(
                BASE_URL + "/issue/"+issue+"/timetracking/workitem/",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), String.class
        );
        return resp.getBody();
    }

    @Override
    public WorkItem[] getIssueWorkItems(String issue) {
        log.info( "requesting work items of {}", issue );

        ResponseEntity< WorkItem[] > resp = ytClient.exchange(
                BASE_URL + "/issue/"+issue+"/timetracking/workitem/",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), WorkItem[].class
        );
        return resp.getBody();
    }

    @Override
    public String createIssue(String project, String summary, String description) {
        log.info( "creating issue: project={}, summary={}, description={}", project, summary, description);

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
            log.error("failed to create issue: failed to extract issue id from response Location header: {}" + response.getHeaders().getLocation());
            throw new RuntimeException();
        }

        log.info("created issue with id = {}", issueId);
        return issueId;
    }

    private final static Logger log = LoggerFactory.getLogger( YoutrackServiceImpl.class );
}
