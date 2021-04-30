package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;

public class DeliveryEvents {

    @Url( value = "deliveries", primary = true )
    public static class Show {
        @Omit
        public Boolean preScroll = false;
        public Show () {}
        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
        }
    }

    @Url( value = "delivery")
    public static class Edit {
        public Edit() {}

        public Edit(Long id) {
            this.id = id;
        }

        public Long id;
    }

    public static class ShowPreview {
        public ShowPreview (HasWidgets parent, Long id) {
            this.parent = parent;
            this.id = id;
        }

        public Long id;
        public HasWidgets parent;
    }

    @Url(value = "delivery_preview", primary = true)
    public static class ShowFullScreen {
        public ShowFullScreen() {}

        public ShowFullScreen(Long deliveryId) {
            this.deliveryId = deliveryId;
        }

        @Name("id")
        public Long deliveryId;
    }

    public static class ShowConciseTable {
        public ShowConciseTable() {}

        public ShowConciseTable(HasWidgets parent, Long parentDeliveryId) {
            this.parent = parent;
            this.parentDeliveryId = parentDeliveryId;
        }

        public HasWidgets parent;
        public Long parentDeliveryId;
    }

    public static class ChangeModel {}
}
