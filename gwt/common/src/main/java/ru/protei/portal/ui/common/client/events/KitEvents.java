package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;

public class KitEvents {

    public static class Create {
        public Create(Long deliveryId, boolean isMilitaryNumbering) {
            this.deliveryId = deliveryId;
            this.isMilitaryNumbering = isMilitaryNumbering;
        }
        public Long deliveryId;
        public boolean isMilitaryNumbering;
    }
}
