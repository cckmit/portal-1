package ru.protei.portal.ui.employeeregistration.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.dict.En_InternalResource;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeRegistrationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Date;
import java.util.HashSet;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;


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
        EmployeeRegistration newEmployeeRegistration = fillDto( new EmployeeRegistration() );
        if (getValidationError(newEmployeeRegistration) != null) {
            showValidationError(newEmployeeRegistration);
            return;
        }
        saveEmployeeRegistration(newEmployeeRegistration);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void showValidationError(EmployeeRegistration employeeRegistration) {
        fireEvent(new NotifyEvents.Show(getValidationError(employeeRegistration), NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError(EmployeeRegistration registration) {
        if (StringUtils.isBlank(registration.getEmployeeFullName()))
            return lang.employeeRegistrationValidationEmployeeFullName();

        if (StringUtils.isBlank(registration.getPosition()))
            return lang.employeeRegistrationValidationPosition();

        if (registration.getEmploymentDate() == null)
            return lang.employeeRegistrationValidationEmploymentDate();

        if (registration.getHeadOfDepartment() == null)
            return lang.employeeRegistrationValidationHeadOfDepartment();

        if (registration.getProbationPeriodMonth() == null || registration.getProbationPeriodMonth() < 1)
            return lang.employeeRegistrationValidationProbationPeriod();

        if (isEmpty(registration.getCuratorsIds()))
            return lang.employeeRegistrationValidationCurators();

        if ( registration.getCuratorsIds().contains(registration.getHeadOfDepartment().getId()))
            return lang.employeeRegistrationValidationHeadOfDepartmentAsCurator();

        if (registration.getAdditionalSoft().length() > ADDITIONAL_SOFT_MAX_LENGTH)
            return lang.employeeRegistrationAdditionalSoftLengthExceed() + " (" + ADDITIONAL_SOFT_MAX_LENGTH +")";

        if (registration.getResourceComment().length() > RESOURCE_COMMENT_MAX_LENGTH )
            return lang.employeeRegistrationResourceCommentLengthExceed() + " (" + RESOURCE_COMMENT_MAX_LENGTH +")";

        return null;
    }

    private void saveEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        view.saveEnabled().setEnabled(false);
        employeeRegistrationService.createEmployeeRegistration(employeeRegistration, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotCreated(), NotifyEvents.NotifyType.ERROR));
                view.saveEnabled().setEnabled(true);
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new Back());
            }
        });
    }


    private EmployeeRegistration fillDto(EmployeeRegistration q) {

        q.setEmployeeFullName(view.fullName().getValue());
        q.setComment(view.comment().getValue());
        q.setWorkplace(view.workplace().getValue());
        q.setPosition(view.position().getValue());
        q.setEmploymentDate(view.employmentDate().getValue());
        if (view.headOfDepartment().getValue() == null)
            q.setHeadOfDepartmentId(null);
        else
            q.setHeadOfDepartmentId(view.headOfDepartment().getValue().getId());
        q.setEquipmentList(view.equipmentList().getValue());
        q.setResourceList(view.resourcesList().getValue());
        q.setPhoneOfficeTypeList(view.phoneOfficeTypeList().getValue());
        q.setWithRegistration(view.withRegistration().getValue());
        q.setEmploymentType(view.employmentType().getValue());

        q.setProbationPeriodMonth( view.probationPeriod().getValue() );
        q.setResourceComment( view.resourceComment().getValue() );
        q.setOperatingSystem( view.operatingSystem().getValue() );
        q.setAdditionalSoft( view.additionalSoft().getValue() );
        q.setCuratorsIds( toSet( view.curators().getValue(), PersonShortView::getId ));

        return q;
    }

    private void clearView() {
        view.fullName().setValue("");
        view.comment().setValue("");
        view.workplace().setValue("");
        view.position().setValue("");
        view.probationPeriod().setValue(null);
        view.resourceComment().setValue(null);
        view.operatingSystem().setValue(null);
        view.additionalSoft().setValue(null);
        view.employmentDate().setValue(new Date());
        view.headOfDepartment().setValue(null);
        view.equipmentList().setValue(new HashSet<>());
        HashSet<En_InternalResource> resources = new HashSet<>();
        resources.add(En_InternalResource.EMAIL);
        view.phoneOfficeTypeList().setValue(new HashSet<>());
        view.resourcesList().setValue(resources);
        view.withRegistration().setValue(true);
        view.employmentType().setValue(En_EmploymentType.FULL_TIME);

        view.positionValidation().setValid(true);
        view.fullNameValidation().setValid(true);
        view.headOfDepartmentValidation().setValid(true);
        view.setEmploymentDateValid(true);
        view.curators().setValue( null );

        view.saveEnabled().setEnabled(true);
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractEmployeeRegistrationEditView view;
    @Inject
    private EmployeeRegistrationControllerAsync employeeRegistrationService;

    private AppEvents.InitDetails initDetails;

    private static final int ADDITIONAL_SOFT_MAX_LENGTH = 512;
    private static final int RESOURCE_COMMENT_MAX_LENGTH = 512;
}
