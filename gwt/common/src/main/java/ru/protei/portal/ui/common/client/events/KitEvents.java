package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Kit;

import java.util.List;
import java.util.function.Consumer;

public class KitEvents {

    @Url( value = "kits", primary = true )
    public static class Show {
        public Show () {}
        public Show(Long deliveryId, Long kitId) {
            this.deliveryId = deliveryId;
            this.kitId = kitId;
        }

        @Name( "delivery" )
        public Long deliveryId;
        @Omit
        public Long kitId;
    }

    public static class Add {
        public Add(long deliveryId) {
            this.deliveryId = deliveryId;
        }
        public Add withBackHandler(Consumer<List<Kit>> backHandler) {
            this.backHandler = backHandler;
            return this;
        }
        public long deliveryId;
        public Consumer<List<Kit>> backHandler;
    }

    public static class Edit {
        public Edit(long kitId) {
            this.kitId = kitId;
        }
        public Edit withBackHandler(Consumer<Kit> backHandler) {
            this.backHandler = backHandler;
            return this;
        }
        public long kitId;
        public Consumer<Kit> backHandler;
    }
}
