package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

/**
 * События по регионам
 */
public class RegionEvents {

    /**
     * Показать grid регионов
     */
    @Url( value = "regions", primary = true )
    public static class Show {

        public Show () {}
    }

    public static class ChangeModel {}
}
