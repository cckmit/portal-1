package ru.protei.portal.jira.handlers;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.JiraIssueEvent;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.jira.factory.JiraEventTypeHandlersFactory;
import ru.protei.portal.jira.utils.CommonUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(value = "/", method = RequestMethod.POST)
public class JiraEventHandlerImpl implements JiraEventHandler {
    private static final Logger log = LoggerFactory.getLogger(JiraEventHandlerImpl.class);

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    @EventListener
    public void onIssueEvent(HttpServletRequest request) {
        String content = "";
        try {
            content = IOUtils.toString(request.getReader());
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonTree = jsonParser.parse(content);

        final JsonObject jsonObject = jsonTree.getAsJsonObject();
        final String eventType = jsonObject.get("webhookEvent").getAsString();
        final IssueEvent issueEvent = CommonUtils.buildIssueEventFromJson(jsonObject);
        JiraEventTypeHandlersFactory.returnHandler(eventType).handle(issueEvent);
    }
}
