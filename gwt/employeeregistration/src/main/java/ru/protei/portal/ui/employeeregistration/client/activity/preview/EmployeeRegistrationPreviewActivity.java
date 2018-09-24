package ru.protei.portal.ui.employeeregistration.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeRegistrationEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_EmployeeEquipmentLang;
import ru.protei.portal.ui.common.client.lang.En_EmploymentTypeLang;
import ru.protei.portal.ui.common.client.lang.En_InternalResourceLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

public abstract class EmployeeRegistrationPreviewActivity implements AbstractEmployeeRegistrationPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.fullScreenContainer = event.parent;
    }

    @Event
    public void onShow( EmployeeRegistrationEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        loadDetails(event.id);
    }

    @Event
    public void onFullScreenShow(EmployeeRegistrationEvents.ShowFullScreen event) {
        if (event.id == null) {
            fireEvent(new Back());
            return;
        }
        fullScreenContainer.clear();
        fullScreenContainer.add( view.asWidget() );
        loadDetails(event.id);
    }

    private void loadDetails(Long id) {
        employeeRegistrationController.getEmployeeRegistration(id, new RequestCallback<EmployeeRegistration>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetItem(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(EmployeeRegistration result) {
                if (result == null) {
                    onError(null);
                    return;
                }
                fillView(result);
            }
        });
    }

    private void fillView( EmployeeRegistration value ) {
        view.setFullName(value.getEmployeeFullName());
        view.setComment(value.getComment());
        view.setWorkplace(value.getWorkplace());
        view.setEmploymentDate(DateFormatter.formatDateOnly(value.getEmploymentDate()));
        view.setCreated(DateFormatter.formatDateTime(value.getCreated()));
        view.setEquipmentList(CollectionUtils.join(value.getEquipmentList(), equipmentLang::getName, ", "));
        view.setResourceList(CollectionUtils.join(value.getResourceList(), resourceLang::getName, ", "));
        view.setPosition(value.getPosition());
        if (value.getHeadOfDepartment() != null)
            view.setHeadOfDepartment(value.getHeadOfDepartment().getDisplayShortName());
        else
            view.setHeadOfDepartment("");

        if (value.getEmploymentType() == null) {
            view.setEmploymentType("");
        } else {
            String employmentType = employmentTypeLang.getName(value.getEmploymentType());
            view.setEmploymentType(employmentType);
        }

        if (value.isWithRegistration())
            view.setWithRegistration(lang.employeeRegistrationEmployeeWithRegistrationTrue());
        else
            view.setWithRegistration(lang.employeeRegistrationEmployeeWithRegistrationFalse());

        view.setState(value.getState());
        view.setIssues(value.getYoutrackIssues());

        fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), value.getId(), false, true) );
    }

    private HasWidgets fullScreenContainer;

    @Inject
    private AbstractEmployeeRegistrationPreviewView view;
    @Inject
    private EmployeeRegistrationControllerAsync employeeRegistrationController;

    @Inject
    private En_EmployeeEquipmentLang equipmentLang;
    @Inject
    private En_InternalResourceLang resourceLang;
    @Inject
    private En_EmploymentTypeLang employmentTypeLang;
    @Inject
    private Lang lang;

}
