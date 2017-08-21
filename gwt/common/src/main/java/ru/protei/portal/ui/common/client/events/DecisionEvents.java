package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

/**
 * Created by serebryakov on 21/08/17.
 */
public class DecisionEvents {

    @Url( value = "decisions", primary = true )
    public static class Show {

        public Show () {}

    }
}
