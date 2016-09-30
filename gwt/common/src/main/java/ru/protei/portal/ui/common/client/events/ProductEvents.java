package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

/**
 * Created by turik on 27.09.16.
 */
public class ProductEvents {

    @Url( value = "products", primary = true )
    public static class Show {

        public Show () {}
    }

}
