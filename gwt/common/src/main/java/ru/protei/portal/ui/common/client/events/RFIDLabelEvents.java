package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.RFIDLabel;

public class RFIDLabelEvents {

    @Url( value = "rfid_labels", primary = true )
    public static class Show {
        public Show () {}
    }

    @Url( value = "rfid_label")
    public static class Edit {
        public Edit() {}

        public Edit(Long id) {
            this.id = id;
        }

        public Long id;
    }

    public static class Change {
        public Change(RFIDLabel item) {
            this.item = item;
        }
        public RFIDLabel item;
    }
}
