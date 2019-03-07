package ru.protei.portal.test.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class SimpleTest {

    public static void main(String[] args) throws URISyntaxException {
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI uri = new URI("https://jira.billing.ru/");
        JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, "protei_tech_user", "FAut>WxJ9q");


        User user = client.getUserClient().getUser("protei_tech_user").claim();

        System.out.println(user.getEmailAddress());
        System.out.println(user.getTimezone());

        for (BasicProject project : client.getProjectClient().getAllProjects().claim()) {
            System.out.println(project.getKey() + ": " + project.getName());
        }

        String jql = "project = PRT";
        SearchRestClient searchRestClient = client.getSearchClient();
        SearchResult result = searchRestClient.searchJql(jql).claim();
        result.getIssues().forEach(issue -> System.out.println(issue));


        Issue testIssue = client.getIssueClient().getIssue("PRT-1").claim();
        testIssue.getComments().forEach(comment -> System.out.println(comment.getAuthor().getDisplayName() + ":" + comment.getBody()));




        testIssue = client.getIssueClient().getIssue("PRT-2").claim();
//        client.getIssueClient().updateIssue("PRT-2",
//                new IssueInputBuilder()
//                        .setFieldInput(new FieldInput(IssueFieldId.COMMENT_FIELD.id, "Comment test"))
//                        .build()).claim();

        client.getIssueClient().addComment(testIssue.getCommentsUri(), Comment.valueOf("rft-89212")).claim();

        //
//        final IssueInputBuilder issueInputParameters = new IssueInputBuilder(testIssue.getProject(), testIssue.getIssueType());
//        issueInputParameters
//                .setSummary("test-back-channel/protei-crm")
//                .setDescription("initial description");
////                .setFieldInput(new FieldInput(IssueFieldId.COMMENT_FIELD.id, "Comment test"));
//
//        Promise<BasicIssue> newIssue = client.getIssueClient().createIssue(issueInputParameters.build());
//
//        BasicIssue issue = newIssue.claim();
//        System.out.println(issue);
//        client.getIssueClient().addComment(issue.getSelf(), Comment.valueOf("Comment test")).claim();




//        ComponentAccessor accessor = new ComponentAccessor();
//        final CommentManager manager = accessor.getCommentManager();
//        String jql = "project = PRT";
//        SearchRestClient searchRestClient = client.getSearchClient();
//        Promise<SearchResult> resultPromise = searchRestClient.searchJql(jql);
//        Iterable<Issue> result = resultPromise.claim().getIssues();

//
//        final Project project = client.getProjectClient().getProject("PRT").claim();
//        final Iterable<BasicComponent> components = project.getComponents();
//        StreamSupport.stream(client.getProjectClient().getProject("PRT").claim().getIssueTypes().spliterator(), false);
//        // Print the result
//        System.out.println(String.format("Your admin user's email address is: %s\r\n", user.getEmailAddress()));
//
//        // Done
//        System.out.println("Example complete. Now exiting.");
//        System.exit(0);
    }
}
