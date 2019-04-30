package ru.protei.portal.jira.factory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import ru.protei.portal.core.model.ent.JiraEndpoint;

import java.net.URI;
import java.net.URISyntaxException;

public class JiraClientFactoryImpl implements JiraClientFactory {

    JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

    public JiraClientFactoryImpl() {
    }

    @Override
    public JiraRestClient create(JiraEndpoint endpoint) {
        try {
            return factory.createWithBasicHttpAuthentication(new URI(endpoint.getServerAddress()), endpoint.getServerLogin(), endpoint.getServerPassword());
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("wrong server uri: " + endpoint.getServerAddress(), e);
        }
    }
}
