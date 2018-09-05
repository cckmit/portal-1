package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.IssueResponse;
import ru.protei.portal.core.model.yt.WorkItem;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class YoutrackServiceImpl implements YoutrackService {
    RestTemplate ytClient;

    HttpHeaders authHeaders;

    @PostConstruct
    public void onInit() {
        ytClient = new RestTemplate(/*Arrays.asList( new HttpMessageConverter< IssueResponse >() {
            @Override
            public boolean canRead( Class< ? > aClass, MediaType mediaType ) {
                return true;
            }

            @Override
            public boolean canWrite( Class< ? > aClass, MediaType mediaType ) {
                return false;
            }

            @Override
            public List<MediaType> getSupportedMediaTypes() {
                return Arrays.asList( MediaType.APPLICATION_JSON );
            }

            @Override
            public IssueResponse read( Class< ? extends IssueResponse > aClass, HttpInputMessage httpInputMessage ) throws IOException, HttpMessageNotReadableException {
                InputStreamReader r = new InputStreamReader( httpInputMessage.getBody() );
                char buf[] = new char[ 256 ];
                r.read( buf );
                return null;
            }

            @Override
            public void write(IssueResponse issueResponse, MediaType mediaType, HttpOutputMessage httpOutputMessage ) throws IOException, HttpMessageNotWritableException {

            }
        } ) )*/);
//        RestTemplateBuilder builder = new RestTemplateBuilder(  );
//        builder.additionalMessageConverters(  );
//        ytClient = builder.build(  );

        authHeaders = new HttpHeaders();
        authHeaders.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
        authHeaders.set( "Authorization", "Bearer perm:c2hhZ2FsZWV2.cGRiMg==.8KZv5OmkR1BKoK6eWmpf2cEm7jVCP9" );
    }

    @Override
    public List<Issue> getTasks(String ytLogin, String date, DateType dateType) {
        String url = "https://youtrack.protei/rest/issue?max=100&with=links&with=Assignee&with=projectShortName&with=Type&with=id&with=entityId&with=Заказчик&with=Номер обращения в CRM&with=resolved&with=created&with=reporterFullName&with=reporterName";

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
                "https://youtrack.protei/rest/issue/"+issue+"?with=links&with=Assignee&with=projectShortName&with=Type&with=id&with=entityId&with=Заказчик&with=Номер обращения в CRM&with=resolved",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), Issue.class
        );
        return resp.getBody();
    }

    @Override
    public String getIssueChangesRaw(String issue) {
        ResponseEntity< String > resp = ytClient.exchange(
                "https://youtrack.protei/rest/issue/"+issue+"/changes",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), String.class
        );
        return resp.getBody();
    }

    @Override
    public ChangeResponse getIssueChanges(String issue) {
        log.info( "requesting changes of {}", issue );

        ResponseEntity< ChangeResponse > resp = ytClient.exchange(
                "https://youtrack.protei/rest/issue/"+issue+"/changes",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), ChangeResponse.class
        );
        return resp.getBody();
    }

    @Override
    public String getIssueWorkItemsRaw(String issue) {
        log.info( "requesting work items of {}", issue );

        ResponseEntity< String > resp = ytClient.exchange(
                "https://youtrack.protei/rest/issue/"+issue+"/timetracking/workitem/",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), String.class
        );
        return resp.getBody();
    }

    @Override
    public WorkItem[] getIssueWorkItems(String issue) {
        log.info( "requesting work items of {}", issue );

        ResponseEntity< WorkItem[] > resp = ytClient.exchange(
                "https://youtrack.protei/rest/issue/"+issue+"/timetracking/workitem/",
                HttpMethod.GET, new HttpEntity<>( authHeaders ), WorkItem[].class
        );
        return resp.getBody();
    }

    private final static Logger log = LoggerFactory.getLogger( YoutrackServiceImpl.class );
}
