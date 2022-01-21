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
        public Long planId;

        public Edit () {
            planId = null;
        }
        public Edit (Long id) {
            this.planId = id;
        }
    }

    public static class EditParams {
        public Plan plan;

        public EditParams(Plan plan) {
            this.plan = plan;
        }
    }

    public static class UpdateParams {
        public Plan plan;

        public UpdateParams(Plan plan) {
            this.plan = plan;
        }
    }

    public static class UpdateIssues {
        public List<CaseShortView> issues;

        public UpdateIssues(List<CaseShortView> issues) {
            this.issues = issues;
        }
    }

    public static class ShowPreview {
        public Long planId;
        public HasWidgets parent;

        public ShowPreview( HasWidgets parent, Long planId ) {
            this.parent = parent;
            this.planId = planId;
        }
    }

    @Url( value = "plan_preview", primary = true )
    public static class ShowFullScreen {
        @Name( "id" )
        public Long planId;

        public ShowFullScreen() {}
        public ShowFullScreen ( Long id )
        {
            this.planId = id;
        }
    }

    public static class ShowUnplannedIssueTable {
        public HasWidgets parent;
        public Long planId;
        public Integer scrollTo;

        public ShowUnplannedIssueTable(HasWidgets parent, Long planId, Integer scrollTo) {
            this.parent = parent;
            this.planId = planId;
            this.scrollTo = scrollTo;
        }
    }

    public static class ShowPlannedIssueTable {
        public HasWidgets parent;
        public List<Plan> planList;
        public Long planId;
        public Integer scrollTo;

        public ShowPlannedIssueTable(HasWidgets parent, List<Plan> planList, Long planId, Integer scrollTo) {
            this.planList = planList;
            this.parent = parent;
            this.planId = planId;
            this.scrollTo = scrollTo;
        }
    }

    public static class AddIssueToPlan {
        public CaseShortView issue;

        public AddIssueToPlan(CaseShortView issue) {
            this.issue = issue;
        }
    }

    public static class AddIssueToUnplannedTable {
        public CaseShortView issue;

        public AddIssueToUnplannedTable(CaseShortView issue) {
            this.issue = issue;
        }
    }

    public static class RemoveIssueFromUnplannedTable {
        public CaseShortView issue;

        public RemoveIssueFromUnplannedTable(CaseShortView issue) {
            this.issue = issue;
        }
    }

    public static class ChangeModel {}

    public static class PersistScroll {}
}
