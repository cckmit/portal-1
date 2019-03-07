package ru.protei.portal.jira.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.utils.JiraHookEventData;

@RestController
public class JiraEventHandlerImpl implements JiraEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(JiraEventHandlerImpl.class);

    @Autowired
    JiraIntegrationService integrationService;


    public JiraEventHandlerImpl() {
        logger.debug("jira webhook handler installed");
    }

    @PostMapping("/webhook")
    public void onIssueEventNoParam(@RequestBody String jsonString,
                             @RequestHeader(value = "Host",required = false) String fromHost,
                             @RequestHeader(value = "X-Real-IP", required = false)String realIP,
                             @PathVariable("stuff")String query
    ) {
        onIssueEvent (jsonString, fromHost, realIP, query);
    }

        @PostMapping("/webhook?{stuff}")
    public void onIssueEvent(@RequestBody String jsonString,
                             @RequestHeader(value = "Host",required = false) String fromHost,
                             @RequestHeader(value = "X-Real-IP", required = false)String realIP,
                             @PathVariable("stuff")String query
                             ) {
        logger.debug("got json string, src-ip={}, host={}, query={}, data: {}", realIP, fromHost, query, jsonString);

        try {
            JiraHookEventData eventData = JiraHookEventData.parse(jsonString);

            if (eventData == null) {
                logger.debug("unable to parse event data, return");
                return;
            }

            logger.debug("parsed data: {}", eventData.toDebugString());

            if (eventData.isCreateIssueEvent())
                integrationService.create(eventData);
            else if (eventData.isUpdateIssueEvent())
                integrationService.updateOrCreate(eventData);
            else
                return;

        }
        catch (Exception e) {
            logger.error("unable to parse json-data", e);
        }

    }
}
