package ru.protei.portal.jira.factory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.apache.commons.io.IOUtils;
import ru.protei.portal.core.model.ent.JiraEndpoint;

import java.util.function.Consumer;
import java.util.function.Function;

public interface JiraClientFactory {
    JiraRestClient create (JiraEndpoint endpoint);

    default void run (JiraEndpoint endpoint, Consumer<JiraRestClient> consumer) {
        JiraRestClient client = null;
        try {
            consumer.accept(client = create(endpoint));
        }
        finally {
            IOUtils.closeQuietly(client);
        }
    }

    default <R> R invoke (JiraEndpoint endpoint, Function<JiraRestClient, R> function) {
        JiraRestClient client = null;
        try {
            return function.apply(client = create(endpoint));
        }
        finally {
            IOUtils.closeQuietly(client);
        }
    }
}
