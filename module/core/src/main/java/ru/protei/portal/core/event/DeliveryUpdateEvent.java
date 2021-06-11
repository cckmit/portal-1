package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Delivery;

public class DeliveryUpdateEvent extends ApplicationEvent implements AbstractDeliveryEvent {
    private Delivery oldDeliveryState;
    private Delivery newDeliveryState;
    private Long personId;

    public DeliveryUpdateEvent(Object source, Delivery oldDeliveryState, Delivery newDeliveryState, Long personId) {
        super(source);
        this.oldDeliveryState = oldDeliveryState;
        this.personId = personId;
        this.source = source;
        this.newDeliveryState = newDeliveryState;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getDeliveryId() {
        return oldDeliveryState.getId();
    }

    public Delivery getOldDeliveryState() {
        return oldDeliveryState;
    }

    public Delivery getNewDeliveryState() {
        return newDeliveryState;
    }
}
