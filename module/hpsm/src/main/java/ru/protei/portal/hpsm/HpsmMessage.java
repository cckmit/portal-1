package ru.protei.portal.hpsm;

/**
 * Created by michael on 24.04.17.
 */
public class HpsmMessage {

    EventSubject subject;
    EventMsg eventMsg;

    public HpsmMessage(EventSubject subject, EventMsg eventMsg) {
        this.subject = subject;
        this.eventMsg = eventMsg;
    }

    public EventSubject getSubject() {
        return subject;
    }

    public EventMsg getEventMsg() {
        return eventMsg;
    }
}
