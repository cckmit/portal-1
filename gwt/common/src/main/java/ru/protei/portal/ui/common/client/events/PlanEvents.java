package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class PlanEvents {

    @Url( value = "plans", primary = true )
    public static class ShowPlans {

        public ShowPlans () {}
    }
}
