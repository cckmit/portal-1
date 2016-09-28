package ru.protei.portal.ui.crm.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

/**
 * Created by turik on 27.09.16.
 */
public class CompanyEvents {

    @Url( value = "companies", primary = true )
    public static class Show {

        public Show () {}

    }
}
