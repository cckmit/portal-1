package ru.protei.portal.ui.plan.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PlanEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

public abstract class PlanPreviewActivity implements AbstractPlanPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onShow( PlanEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.planId );
        view.showFullScreen( false );
    }

    @Event
    public void onShow( PlanEvents.ShowFullScreen event ) {
        initDetails.parent.clear();


        if (!policyService.hasPrivilegeFor(En_Privilege.PLAN_VIEW)) {
            fireEvent(new ForbiddenEvents.Show(initDetails.parent));
            return;
        }

        initDetails.parent.add( view.asWidget() );

        fillView( event.planId );
        view.showFullScreen( true );
    }

    @Override
    public void onFullScreenPreviewClicked() {
        if ( plan == null ) return;
        fireEvent( new PlanEvents.ShowFullScreen( plan.getId() ) );
    }

    @Override
    public void onGoToPlansClicked() {
        fireEvent(new PlanEvents.ShowPlans(true));
    }

    private void fillView( Long id ) {
        if (id == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        planService.getPlanWithIssues( id, new RequestCallback<Plan>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Plan value ) {
                fillView( value );
            }
        } );
    }

    private void fillView(Plan value) {
        this.plan = value;

        view.setHeader( lang.planHeader(value.getId().toString()) );
        view.setName( value.getName() );
        view.setCreatedBy(lang.createBy(value.getCreatorShortName(), DateFormatter.formatDateTime(value.getCreated())));
        view.setPeriod(DateFormatter.formatDateOnly(value.getStartDate()) + " - " + DateFormatter.formatDateOnly(value.getFinishDate()));

        String issues = "";

        if (plan.getIssueList() != null) {
            for (CaseShortView caseShortView : plan.getIssueList()) {
                issues += caseShortView.getCaseNumber() + " " + caseShortView.getName() + "<br>";
            }
        }
        view.setIssues(issues);
    }

    private boolean isSlaContainerVisible(List<ProjectSla> projectSlas) {
        if (CollectionUtils.isEmpty(projectSlas)) {
            return false;
        }

        if (projectSlas.stream().allMatch(ProjectSla::isEmpty)) {
            return false;
        }

        return true;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractPlanPreviewView view;
    @Inject
    PlanControllerAsync planService;
    @Inject
    PolicyService policyService;

    private Plan plan;

    private AppEvents.InitDetails initDetails;
}
