package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;

public class PcbOrderEvents {

    @Url( value = "pcb_orders", primary = true )
    public static class Show {
        public Show () {}
        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
        }

        @Omit
        public Boolean preScroll = false;
    }

    @Url( value = "pcb_order_create")
    public static class Create {
        public Create() {}
    }

    @Url( value = "pcb_order")
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

    public static class Change {
        public Change(Long id) {
            this.id = id;
        }

        public Long id;
    }
}
