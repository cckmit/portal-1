package ru.protei.portal.core.service.events;

import ru.protei.portal.core.event.*;

public class EventDeliveryAssemblerServiceMock implements EventDeliveryAssemblerService {
    @Override
    public void onDeliveryCreateEvent(DeliveryCreateEvent event) {}

    @Override
    public void onDeliveryUpdateEvent(DeliveryUpdateEvent event) {}

    @Override
    public void onDeliveryNameAndDescriptionEvent(DeliveryNameAndDescriptionEvent event) {}

    @Override
    public void onDeliveryCommentEvent(DeliveryCommentEvent event) {}

    @Override
    public void onDeliveryAttachmentEvent(DeliveryAttachmentEvent event) {}
}
