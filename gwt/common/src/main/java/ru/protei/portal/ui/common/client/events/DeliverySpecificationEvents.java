package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class DeliverySpecificationEvents {

    @Url(value = "delivery_specifications", primary = true)
    public static class Show {
        public Show() {
        }
    }

    @Url(value = "delivery_specifications_import", primary = true)
    public static class ShowImport {
        public ShowImport() {
        }
    }
}
