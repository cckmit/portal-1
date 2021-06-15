package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

public class DeliveryCreateEvent extends ApplicationEvent implements AbstractDeliveryEvent {
    private Long personId;
    private Long deliveryId;

    public DeliveryCreateEvent(Object source, Long personId, Long deliveryId) {
        super(source);
        this.personId = personId;
        this.deliveryId = deliveryId;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getDeliveryId() {
        return deliveryId;
    }

    @Override
    public boolean isCreateEvent() {
        return true;
    }
}
