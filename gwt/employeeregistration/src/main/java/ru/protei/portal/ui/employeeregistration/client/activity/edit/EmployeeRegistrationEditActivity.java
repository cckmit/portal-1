package ru.protei.portal.ui.employeeregistration.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeRegistrationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Date;
import java.util.HashSet;


public abstract class EmployeeRegistrationEditActivity implements Activity, AbstractEmployeeRegistrationEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(EmployeeRegistrationEvents.Create event) {
        clearView();

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    @Override
    public void onSaveClicked() {
        if (!isViewValid()) {
            validateView();
            return;
        }
        EmployeeRegistration newEmployeeRegistration = fillDto();
        saveEmployeeRegistration(newEmployeeRegistration);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private boolean isViewValid() {
        if (StringUtils.isBlank(view.fullName().getValue()))
            return false;
        if (StringUtils.isBlank(view.post().getValue()))
            return false;
        if (view.headOfDepartment().getValue() == null)
            return false;
        if (view.employmentDate().getValue() == null)
            return false;
        return true;
    }

    private void validateView() {
        boolean fullNameValid = !StringUtils.isBlank(view.fullName().getValue());
        view.fullNameValidation().setValid(fullNameValid);

        boolean postValid = !StringUtils.isBlank(view.post().getValue());
        view.postValidation().setValid(postValid);

        boolean employmentDateValid = view.employmentDate().getValue() != null;
        view.setEmploymentDateValid(employmentDateValid);

        boolean headOfDepartmentValid = view.headOfDepartment().getValue() != null;
        view.headOfDepartmentValidation().setValid(headOfDepartmentValid);
    }

    private void saveEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        employeeRegistrationService.createEmployeeRegistration(employeeRegistration, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotCreated(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new Back());
            }
        });
    }


    private EmployeeRegistration fillDto() {
        EmployeeRegistration q = new EmployeeRegistration();
        q.setEmployeeFullName(view.fullName().getValue());
        q.setComment(view.comment().getValue());
        q.setWorkplaceInfo(view.workplaceInfo().getValue());
        q.setPost(view.post().getValue());
        q.setEmploymentDate(view.employmentDate().getValue());
        if (view.headOfDepartment().getValue() == null)
            q.setHeadOfDepartmentId(null);
        else
            q.setHeadOfDepartmentId(view.headOfDepartment().getValue().getId());
        q.setEquipmentList(view.equipmentList().getValue());
        q.setResourceList(view.resourcesList().getValue());
        q.setWithRegistration(view.withRegistration().getValue());
        q.setEmploymentType(view.employmentType().getValue());
        return q;
    }

    private void clearView() {
        view.fullName().setValue("");
        view.comment().setValue("");
        view.workplaceInfo().setValue("");
        view.post().setValue("");
        view.employmentDate().setValue(new Date());
        view.headOfDepartment().setValue(null);
        view.equipmentList().setValue(new HashSet<>());
        view.resourcesList().setValue(new HashSet<>());
        view.withRegistration().setValue(true);
        view.employmentType().setValue(En_EmploymentType.FULL_TIME);

        view.postValidation().setValid(true);
        view.fullNameValidation().setValid(true);
        view.headOfDepartmentValidation().setValid(true);
        view.setEmploymentDateValid(true);
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractEmployeeRegistrationEditView view;
    @Inject
    private EmployeeRegistrationControllerAsync employeeRegistrationService;

    private AppEvents.InitDetails initDetails;
}
