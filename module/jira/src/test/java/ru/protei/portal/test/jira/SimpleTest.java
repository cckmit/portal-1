package ru.protei.portal.test.jira;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.atlassian.util.concurrent.Promise;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.StreamSupport;

public class SimpleTest {

    public static void main(String[] args) throws URISyntaxException {
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI uri = new URI("https://jira.billing.ru/");
        JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, "protei_tech_user", "FAut>WxJ9q");


        Promise<User> promise = client.getUserClient().getUser("protei_tech_user");
        User user = promise.claim();

        for (BasicProject project : client.getProjectClient().getAllProjects().claim()) {
            System.out.println(project.getKey() + ": " + project.getName());
        }

        ComponentAccessor accessor = new ComponentAccessor();
        final CommentManager manager = accessor.getCommentManager();
        String jql = "project = PRT";
        SearchRestClient searchRestClient = client.getSearchClient();
        Promise<SearchResult> resultPromise = searchRestClient.searchJql(jql);
        Iterable<Issue> result = resultPromise.claim().getIssues();


        final Project project = client.getProjectClient().getProject("PRT").claim();
        final Iterable<BasicComponent> components = project.getComponents();
        StreamSupport.stream(client.getProjectClient().getProject("PRT").claim().getIssueTypes().spliterator(), false);
        // Print the result
        System.out.println(String.format("Your admin user's email address is: %s\r\n", user.getEmailAddress()));

        // Done
        System.out.println("Example complete. Now exiting.");
        System.exit(0);
    }
}
