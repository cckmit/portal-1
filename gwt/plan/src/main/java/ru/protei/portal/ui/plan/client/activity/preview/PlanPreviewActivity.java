package ru.protei.portal.ui.plan.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

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
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.add(view.asWidget());

        fillView( event.planId );
        view.showFullScreen( true );
    }

    @Override
    public void onFullScreenPreviewClicked() {
        if ( plan == null ) return;
        fireEvent( new PlanEvents.ShowFullScreen( plan.getId() ) );
    }

    @Override
    public void onItemClicked(CaseShortView value) {
        fireEvent(new IssueEvents.Edit(value.getCaseNumber()));
    }

    @Override
    public void onGoToPlansClicked() {
        fireEvent(new PlanEvents.ShowPlans(true));
    }

    private void fillView(Long id) {
        if (id == null) {
            fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        planService.getPlanWithIssues(id, new FluentCallback<Plan>()
                .withError(throwable -> fireEvent(new ErrorPageEvents.ShowNotFound(initDetails.parent, lang.errPlanNotFound())))
                .withSuccess(this::fillView)
        );
    }

    private void fillView(Plan value) {
        this.plan = value;

        view.setHeader( lang.planHeader(value.getId().toString()) );
        view.setName( value.getName() );
        view.setCreatedBy(lang.createBy(value.getCreatorShortName(), DateFormatter.formatDateTime(value.getCreated())));
        view.setPeriod(DateFormatter.formatDateOnly(value.getStartDate()) + " - " + DateFormatter.formatDateOnly(value.getFinishDate()));

        loadTable(value.getIssueList());
    }

    private void loadTable(List<CaseShortView> issuesList){
        view.clearRecords();
        view.putRecords(issuesList);
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
