package ru.protei.portal.ui.education.client.activity.education;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EducationEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;

public abstract class EducationActivity implements Activity, AbstractEducationActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(EducationEvents.Show event) {
        boolean isWorker = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_VIEW);
        boolean isAdmin = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_CREATE);
        boolean hasAccess = isWorker || isAdmin;
        if (!hasAccess) {
            fireEvent(new ForbiddenEvents.Show(initDetails.parent));
            return;
        }
        showView();
        if (isAdmin) {
            view.toggleButtonVisibility().setVisible(true);
            showAdminView();
        } else {
            view.toggleButtonVisibility().setVisible(false);
            showWorkerView();
        }
    }

    @Override
    public void onToggleViewClicked() {
        if (isAdminShowed) {
            showWorkerView();
        } else {
            showAdminView();
        }
    }

    @Override
    public void onReloadClicked() {
        if (isAdminShowed) {
            showAdminView();
        } else {
            showWorkerView();
        }
    }

    private void showView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    private void showWorkerView() {
        isAdminShowed = false;
        fireEvent(new EducationEvents.ShowWorker(view.container()));
    }

    private void showAdminView() {
        isAdminShowed = true;
        fireEvent(new EducationEvents.ShowAdmin(view.container()));
    }

    @Inject
    AbstractEducationView view;
    @Inject
    PolicyService policyService;

    private boolean isAdminShowed = false;
    private AppEvents.InitDetails initDetails;
}
