package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

public class CardBatchCreateEvent extends ApplicationEvent implements AbstractCardBatchEvent {
    private Long personId;
    private Long cardBatchId;

    public CardBatchCreateEvent(Object source, Long personId, Long cardBatchId) {
        super(source);
        this.personId = personId;
        this.cardBatchId = cardBatchId;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getCardBatchId() {
        return cardBatchId;
    }

    @Override
    public boolean isCreateEvent() {
        return true;
    }
}
