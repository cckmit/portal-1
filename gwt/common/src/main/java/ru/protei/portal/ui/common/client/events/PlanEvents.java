package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;

public class PlanEvents {

    @Url( value = "plans", primary = true )
    public static class ShowPlans {

        public ShowPlans () {}
    }

    public static class CreatePlan {
        public CreatePlan(HasWidgets parent) {
            this.parent = parent;
        }
        public HasWidgets parent;
    }
}
