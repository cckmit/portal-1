package ru.protei.portal.test.jira;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.atlassian.util.concurrent.Promise;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.StreamSupport;

public class SimpleTest {
    @Test
    public void simpleTest() throws URISyntaxException {
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI uri = new URI("https://jira.billing.ru/");
        JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, "protei_tech_user", "FAut>WxJ9q");


        Promise<User> promise = client.getUserClient().getUser("protei_tech_user");
        User user = promise.claim();

        for (BasicProject project : client.getProjectClient().getAllProjects().claim()) {
            System.out.println(project.getKey() + ": " + project.getName());
        }

        client.getProjectClient().getProject("PRT").claim()

        StreamSupport.stream(client.getProjectClient().getProject("PRT").claim().getIssueTypes().spliterator(), false)


        Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql("project = PRT AND status in (Active, Resolved) ORDER BY assignee, resolutiondate");

        for (Issue issue : searchJqlPromise.claim().getIssues()) {
            System.out.println(issue.getSummary());
        }

        // Print the result
        System.out.println(String.format("Your admin user's email address is: %s\r\n", user.getEmailAddress()));

        // Done
        System.out.println("Example complete. Now exiting.");
        System.exit(0);
    }
}

