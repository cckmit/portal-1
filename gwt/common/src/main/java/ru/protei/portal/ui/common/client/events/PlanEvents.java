package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
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

        public ShowPreview( HasWidgets parent, Long planId ) {
            this.parent = parent;
            this.planId = planId;
        }

        public HasWidgets parent;
        public Long planId;
    }

    @Url( value = "plan_preview", primary = true )
    public static class ShowFullScreen {

        public ShowFullScreen() {}

        public ShowFullScreen ( Long id )
        {
            this.planId = id;
        }

        @Name( "id" )
        public Long planId;
    }
}
