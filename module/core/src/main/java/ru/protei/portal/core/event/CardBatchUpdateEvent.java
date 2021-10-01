package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CardBatch;

public class CardBatchUpdateEvent extends ApplicationEvent implements AbstractCardBatchEvent {
    private CardBatch oldCardBatch;
    private CardBatch newCardBatch;
    private Long personId;

    public CardBatchUpdateEvent(Object source, CardBatch oldCardBatch, CardBatch newCardBatch, Long personId) {
        super(source);
        this.oldCardBatch = oldCardBatch;
        this.personId = personId;
        this.source = source;
        this.newCardBatch = newCardBatch;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getCardBatchId() {
        return oldCardBatch.getId();
    }

    public CardBatch getOldCardBatch() {
        return oldCardBatch;
    }

    public CardBatch getNewCardBatch() {
        return newCardBatch;
    }
}
