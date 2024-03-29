package ru.protei.portal.ui.education.client.activity.education;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EducationEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;

import static ru.protei.portal.ui.education.client.util.AccessUtil.*;

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
        if (!hasAccess(policyService)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }
        showView();
        if (isAdmin(policyService)) {
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
