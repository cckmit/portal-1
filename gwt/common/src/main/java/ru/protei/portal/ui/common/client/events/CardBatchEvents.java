package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;

public class CardBatchEvents {

    @Url( value = "card_batches", primary = true )
    public static class Show {
        public Show () {}
        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
        }

        @Omit
        public Boolean preScroll = false;
    }

    @Url( value = "card_batch_create")
    public static class Create {
        public Create() {}
    }

    @Url( value = "card_batch")
    public static class Edit {
        public Edit() {}

        public Edit(Long id) {
            this.id = id;
        }

        public Long id;
    }
}
