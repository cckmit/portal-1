package ru.protei.portal.ui.casestate.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

public abstract class CaseStateTableActivity implements Activity,
        AbstractCaseStateTableActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setAnimation(animation);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.init = initDetails;
    }

    @Event
    public void onShow(CaseStateEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.CASE_STATES_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        init.parent.clear();
        init.parent.add(view.asWidget());

        requestRecords();
    }

    @Event
    public void onClosePreview(CaseStateEvents.ClosePreview event) {
        animation.closeDetails();
    }

    @Event
    public void onUpdateItem(CaseStateEvents.UpdateItem changedCaseState) {
        view.updateRow(changedCaseState.caseState);
    }

    @Override
    public void onItemClicked(CaseState value) {
        showPreview(value);
    }

    @Override
    public void onEditClicked(CaseState value) {
        showPreview(value);
    }

    private void requestRecords() {
        caseStateService.getCaseStates(En_CaseType.CRM_SUPPORT, new RequestCallback<List<CaseState>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<CaseState> result) {
                view.clearRecords();
                view.setData(result);
            }
        });
    }

    private void showPreview(CaseState value) {
        if (value == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new CaseStateEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }


    @Inject
    AbstractCaseStateTableView view;
    @Inject
    TableAnimation animation;
    @Inject
    CaseStateControllerAsync caseStateService;
    @Inject
    PolicyService policyService;

    @Inject
    Lang lang;

    private AppEvents.InitDetails init;
}
