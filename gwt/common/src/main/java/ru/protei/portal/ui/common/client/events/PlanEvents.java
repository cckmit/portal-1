package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;

public class PlanEvents {

    @Url( value = "plans", primary = true )
    public static class ShowPlans {
        @Omit
        public Boolean preScroll = false;

        public ShowPlans () {}
        public ShowPlans (Boolean preScroll) {this.preScroll = preScroll;}
    }

    public static class CreatePlan {
        public CreatePlan(HasWidgets parent) {
            this.parent = parent;
        }
        public HasWidgets parent;
    }

    @Url( value = "plan")
    public static class Edit {

        public Edit () {
            planId = null;
        }

        public Edit (Long id) {
            this.planId = id;
        }

        public Long planId;
    }

    public static class ShowPreview {

        public ShowPreview( HasWidgets parent, Long planId, boolean isShouldWrap ) {
            this.parent = parent;
            this.productId = planId;
            this.isShouldWrap = isShouldWrap;
        }

        public HasWidgets parent;
        public Long productId;
        public boolean isShouldWrap;
    }
}
