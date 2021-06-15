package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.util.DiffResult;

public class DeliveryNameAndDescriptionEvent extends ApplicationEvent implements AbstractDeliveryEvent {

    private Long deliveryId;
    private DiffResult<String> name;
    private DiffResult<String> info;
    private Long personId;

    public DeliveryNameAndDescriptionEvent(
            Object source,
            Long deliveryId,
            DiffResult<String> name,
            DiffResult<String> info,
            Long personId) {
        super(source);
        this.deliveryId = deliveryId;
        this.name = name;
        this.info = info;
        this.personId = personId;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getDeliveryId() {
        return deliveryId;
    }

    public DiffResult<String> getName() {
        return name;
    }

    public DiffResult<String> getInfo() {
        return info;
    }
}
