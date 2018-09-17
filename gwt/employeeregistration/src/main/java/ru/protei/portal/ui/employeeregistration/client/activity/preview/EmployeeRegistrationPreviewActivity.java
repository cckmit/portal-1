package ru.protei.portal.ui.employeeregistration.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.EmployeeRegistrationEvents;
import ru.protei.portal.ui.common.client.lang.En_EmployeeEquipmentLang;
import ru.protei.portal.ui.common.client.lang.En_EmploymentTypeLang;
import ru.protei.portal.ui.common.client.lang.En_InternalResourceLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class EmployeeRegistrationPreviewActivity implements AbstractEmployeeRegistrationPreviewActivity, Activity {
    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( EmployeeRegistrationEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.employeeRegistration);
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
    }

    @Inject
    private AbstractEmployeeRegistrationPreviewView view;

    @Inject
    private En_EmployeeEquipmentLang equipmentLang;
    @Inject
    private En_InternalResourceLang resourceLang;
    @Inject
    private En_EmploymentTypeLang employmentTypeLang;
    @Inject
    private Lang lang;

}
