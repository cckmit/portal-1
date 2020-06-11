package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

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

    public static class ShowUnassignedIssueTable {
        public HasWidgets parent;
        public Long planId;
        public ShowUnassignedIssueTable() {}
        public ShowUnassignedIssueTable(HasWidgets parent, Long planId) {
            this.parent = parent;
            this.planId = planId;
        }
    }

    public static class ShowAssignedIssueTable {
        public HasWidgets parent;
        public List<Plan> planList;
        public Long planId;
        public ShowAssignedIssueTable() {}
        public ShowAssignedIssueTable(HasWidgets parent, List<Plan> planList, Long planId) {
            this.planList = planList;
            this.parent = parent;
            this.planId = planId;
        }
    }

    public static class UpdateAssignedIssueTable {
        public List<CaseShortView> issuesList;
        public UpdateAssignedIssueTable() {}
        public UpdateAssignedIssueTable(List<CaseShortView> issuesList) {
            this.issuesList = issuesList;
        }
    }
}
