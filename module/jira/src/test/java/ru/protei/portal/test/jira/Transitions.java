package ru.protei.portal.test.jira;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import ru.protei.portal.jira.utils.CustomJiraIssueParser;

import java.net.URI;
import java.net.URISyntaxException;

public class Transitions {

    public static void main(String[] args) throws URISyntaxException {
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI uri = new URI("https://jira.billing.ru/");
        JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, "protei_tech_user", "FAut>WxJ9q");

        User user = client.getUserClient().getUser("protei_tech_user").claim();

        client.getIssueClient().getCreateIssueMetadata(new GetCreateIssueMetadataOptionsBuilder().withExpandedIssueTypesFields().build()).claim()
                .forEach(cimProject -> cimProject.getIssueTypes().forEach(it->it.getFields().get(IssueFieldId.STATUS_FIELD.id).getAllowedValues()));
//        .forEach(cimProject ->
//                cimProject.getIssueTypes().forEach(
//                        it -> it.getFields().get(CustomJiraIssueParser.CUSTOM_FIELD_SEVERITY).getAllowedValues()
//                                .forEach(v -> System.out.println(((CustomFieldOption)v).getValue()))
//                )
//        );
//
//        System.out.println(user.getEmailAddress());
//        System.out.println(user.getTimezone());

        /*
        Issue issue = client.getIssueClient().getIssue("PRT-2").claim();

        // change severity
        IssueInputBuilder builder = new IssueInputBuilder();
        builder.setFieldValue(CustomJiraIssueParser.CUSTOM_FIELD_SEVERITY, ComplexIssueInputFieldValue.with("value", "90-Suggestion"));
        client.getIssueClient().updateIssue(issue.getKey(), builder.build()).claim();


        // transition (status change)
        Iterable<Transition> transitions = client.getIssueClient().getTransitions(issue).claim();
        transitions.forEach(transition -> System.out.println(transition));
        client.getIssueClient().transition(issue, new TransitionInput(transitions.iterator().next().getId())).claim();
        */
    }
}
