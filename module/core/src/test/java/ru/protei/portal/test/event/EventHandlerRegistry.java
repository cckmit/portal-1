package ru.protei.portal.test.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 04.05.17.
 */
public class EventHandlerRegistry {

    private static Logger logger = LoggerFactory.getLogger(EventHandlerRegistry.class);

    public List<AssembledCaseEvent> assembledCaseEvents;
    public List<CaseCommentEvent> commentEvents;

    public EventHandlerRegistry() {
        this.commentEvents = new ArrayList<>();
        this.assembledCaseEvents = new ArrayList<>();
    }

    @EventListener
    public void handleCaseObjEvent (AssembledCaseEvent event) {
        logger.debug("received case-object event, case {}", event.getCaseObject());
        this.assembledCaseEvents.add(event);
    }

    @EventListener
    public void handleCaseCommentEvent (CaseCommentEvent event) {
        logger.debug("received case-comment event, comment {}", event.getCaseComment());
        this.commentEvents.add(event);
    }
}
