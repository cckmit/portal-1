package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Kit;

import java.util.List;

public class KitEvents {

    @Url( value = "kits", primary = true )
    public static class Show {
        public Show () {}
        public Show(Long deliveryId) {
            this.deliveryId = deliveryId;
        }

        @Name( "delivery" )
        public Long deliveryId;
    }

    public static class Add {
        public Add(long deliveryId, long stateId) {
            this.deliveryId = deliveryId;
            this.stateId = stateId;
        }
        public long deliveryId;
        public long stateId;
    }

    public static class Added {
        public Added(List<Kit> kits, Long deliveryId) {
            this.kits = kits;
            this.deliveryId = deliveryId;
        }
        public Long deliveryId;
        public List<Kit> kits;
    }

}
