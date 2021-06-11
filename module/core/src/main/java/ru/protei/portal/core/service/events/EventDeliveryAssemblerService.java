package ru.protei.portal.core.service.events;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.*;

public interface EventDeliveryAssemblerService {
    @EventListener
    void onDeliveryCreateEvent(DeliveryCreateEvent event);

    @EventListener
    void onDeliveryUpdateEvent(DeliveryUpdateEvent event);

    @EventListener
    void onDeliveryNameAndDescriptionEvent(DeliveryNameAndDescriptionEvent event);

    @EventListener
    void onDeliveryCommentEvent(DeliveryCommentEvent event);

    @EventListener
    void onDeliveryAttachmentEvent(DeliveryAttachmentEvent event);
}
