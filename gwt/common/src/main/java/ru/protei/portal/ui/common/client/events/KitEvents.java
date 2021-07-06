package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.ent.Kit;

import java.util.List;

public class KitEvents {

    public static class Add {
        public Add(Long deliveryId) {
            this.deliveryId = deliveryId;
        }
        public Long deliveryId;
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
