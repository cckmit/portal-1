package ru.protei.portal.jira.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.utils.JiraHookEventData;
import ru.protei.portal.jira.utils.JiraHookEventType;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@RestController
public class JiraEventHandlerImpl {
    private static final Logger logger = LoggerFactory.getLogger(JiraEventHandlerImpl.class);

    @Autowired
    JiraIntegrationService integrationService;

    Map<JiraHookEventType, Consumer<JiraHookEventData>> handlersMap;
    Consumer<JiraHookEventData> defaultHandler;

    public JiraEventHandlerImpl() {
        logger.debug("jira webhook handler installed");
    }


    @PostConstruct
    private void init () {
        handlersMap = new HashMap<>();
        handlersMap.put(JiraHookEventType.ISSUE_CREATED, evt -> integrationService.create(evt));
        handlersMap.put(JiraHookEventType.ISSUE_UPDATED, evt -> integrationService.updateOrCreate(evt));
        handlersMap.put(JiraHookEventType.COMMENT_CREATED, evt -> logger.debug("skip comment-created event"));
        defaultHandler = evt -> logger.debug("has no handler for event {}, skip", evt.getEventType());
    }


    @PostMapping("/webhook")
    public void onIssueEvent(@RequestBody String jsonString,
                             @RequestHeader(value = "Host",required = false) String fromHost,
                             @RequestHeader(value = "X-Real-IP", required = false)String realIP,
                             HttpServletRequest request
                             ) {
        logger.info("got request from JIRA, src-ip={}, host={}, query={}", realIP, fromHost, request.getQueryString());
        logger.debug("data: {}", jsonString);

        try {
            JiraHookEventData eventData = JiraHookEventData.parse(jsonString);

            if (eventData == null) {
                logger.warn("no valid data, return");
                return;
            }

            logger.debug("parsed data: {}", eventData.toDebugString());

            /**
             * Важное замечание: в каждом событии приходит довольно много информации, но зато она очень подробная и полная
             * Событие update или create всегда содержат в объекте Issue все комментарии и описание всех вложений,
             * что на самом деле довольно круто.
             * Т.е. фактически можно делать обработку только для событий created/updated для Issue,
             * при этом иметь всю информацию для синхронизации нашей модели.
             * Вложения (attachments) требуют отдельной обработки, поскольку они естественно представлены
             * в виде описания (откуда брать).
             * Еще есть идея добавить в комментарий или сам case-object (ext-app-data) структуру для
             * хранения всех ID комментариев, которые мы получали от них, а также attachment's,
             * чтобы была возможность при каждом событии проводить полную синхронизацию объекта
             * и добавлять недостающие данные.
             *
             * JIRA позволяет редактировать комментарий, но думаю, что мы пока это не будем учитывать в нашей системе.
             * Еще, кстати, получается нам нужно фильтровать наши комментарии, которые мы им сами отправляем,
             * чтобы не получать петли, когда наш комментарий опять свалится к нам и мы его еще раз добавим.
             */

            handlersMap.getOrDefault(eventData.getEventType(), defaultHandler).accept(eventData);
        }
        catch (Exception e) {
            logger.error("unable to parse json-data", e);
        }
    }
}
