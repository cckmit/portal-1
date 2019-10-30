package ru.protei.portal.core.service;

import org.springframework.scheduling.annotation.Scheduled;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.ent.Person;

public interface EventAssemblerService {

    void publishEvent(CaseObjectEvent event);

    void publishEvent(CaseCommentEvent event);

//    void publishEvent(CaseObjectCommentEvent event);

    void publishEvent(CaseAttachmentEvent event);

    AssembledCaseEvent getEvent(Person person, Long caseId);

//    void forcePublishCaseRelatedEvents(Long caseId);

    int getEventsCount();

//    @Scheduled(fixedRate = EventAssemblerServiceImpl.SCHEDULE_TIME)
    void checkEventsMap();
}
