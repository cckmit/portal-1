package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.ent.Kit;

import java.util.List;

public class KitEvents {

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

    public static class Changed {
        public Changed(Long deliveryId) {
            this.deliveryId = deliveryId;
        }
        public Long deliveryId;
    }

    public static class Edit {
        public Edit(long kitId) {
            this.kitId = kitId;
        }
        public long kitId;
    }
}
