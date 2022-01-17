package ru.protei.portal.ui.employeeregistration.client.activity.create;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeRegistrationEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.employeedepartment.EmployeeDepartmentModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CrmConstants.Company.*;


public abstract class EmployeeRegistrationCreateActivity implements Activity, AbstractEmployeeRegistrationCreateActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setDepartmentModel(departmentModel);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(EmployeeRegistrationEvents.Create event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_REGISTRATION_CREATE)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        departmentModel.refreshOptions(null);
        clearView();

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
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

    @Override
    public void validateLimitedFields() {
        if (view.position().getValue() != null) {
            view.positionErrorLabelVisibility().setVisible(view.position().getValue().length() > CrmConstants.EmployeeRegistration.POSITION_MAX_LENGTH);
        }

        if (view.workplace().getValue() != null) {
            view.workplaceErrorLabelVisibility().setVisible(view.workplace().getValue().length() > CrmConstants.EmployeeRegistration.WORKPLACE_MAX_LENGTH);
        }

        if (view.operatingSystem().getValue() != null) {
            view.operatingSystemErrorLabelVisibility().setVisible(view.operatingSystem().getValue().length() > CrmConstants.EmployeeRegistration.OPERATING_SYSTEM_MAX_LENGTH);
        }

        if (view.additionalSoft().getValue() != null) {
            view.additionalSoftErrorLabelVisibility().setVisible(view.additionalSoft().getValue().length() > CrmConstants.EmployeeRegistration.ADDITIONAL_SOFT_MAX_LENGTH);
        }

        if (view.resourceComment().getValue() != null) {
            view.resourceCommentErrorLabelVisibility().setVisible(view.resourceComment().getValue().length() > CrmConstants.EmployeeRegistration.RESOURCE_COMMENT_MAX_LENGTH);
        }

        view.saveEnabled().setEnabled(
                view.position().getValue().length() <= CrmConstants.EmployeeRegistration.POSITION_MAX_LENGTH
                        && view.workplace().getValue().length() <= CrmConstants.EmployeeRegistration.WORKPLACE_MAX_LENGTH
                        && view.operatingSystem().getValue().length() <= CrmConstants.EmployeeRegistration.OPERATING_SYSTEM_MAX_LENGTH
                        && view.additionalSoft().getValue().length() <= CrmConstants.EmployeeRegistration.ADDITIONAL_SOFT_MAX_LENGTH
                        && view.resourceComment().getValue().length() <= CrmConstants.EmployeeRegistration.RESOURCE_COMMENT_MAX_LENGTH
        );
    }

    @Override
    public void onHeadOfDepartmentChanged() {
        view.departmentEnabled().setEnabled(true);
        view.department().setValue(null);
        departmentModel.refreshOptions(view.headOfDepartment().getValue() == null ? null : view.headOfDepartment().getValue().getId());
    }

    @Override
    public void onCompanySelected() {
        if (isSelectProteiOrProteiST(view.company().getValue())) {
            setPhoneEquipmentEnabled(true);
            Set<En_PhoneOfficeType> value = view.phoneOfficeTypeList().getValue();
            value.add(En_PhoneOfficeType.OFFICE);
            view.phoneOfficeTypeList().setValue(value);
        } else {
            setPhoneEquipmentEnabled(false);
            Set<En_EmployeeEquipment> value = view.equipmentList().getValue();
            value.remove(En_EmployeeEquipment.TELEPHONE);
            view.equipmentList().setValue(value);
            view.phoneOfficeTypeList().setValue(new HashSet<>());
        }
    }

    @Override
    public void onIDEChanged() {
        if (view.ide().getValue()) {
            view.setFocusOnAdditionalSoft();
        }
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

        if (registration.getCompanyId() == null)
            return lang.employeeRegistrationValidationCompany();

        if (registration.getHeadOfDepartment() == null)
            return lang.employeeRegistrationValidationHeadOfDepartment();

        if (registration.getProbationPeriodMonth() == null || registration.getProbationPeriodMonth() < 1)
            return lang.employeeRegistrationValidationProbationPeriod();

        if (isEmpty(registration.getCuratorsIds()))
            return lang.employeeRegistrationValidationCurators();

        if ( registration.getCuratorsIds().contains(registration.getHeadOfDepartment().getId()))
            return lang.employeeRegistrationValidationHeadOfDepartmentAsCurator();

        if (registration.getPosition().length() > CrmConstants.EmployeeRegistration.POSITION_MAX_LENGTH) {
            return lang.employeeRegistrationPositionExceed(CrmConstants.EmployeeRegistration.POSITION_MAX_LENGTH);
        }

        if (registration.getWorkplace().length() > CrmConstants.EmployeeRegistration.WORKPLACE_MAX_LENGTH) {
            return lang.employeeRegistrationWorkplaceExceed(CrmConstants.EmployeeRegistration.WORKPLACE_MAX_LENGTH);
        }

        if (registration.getOperatingSystem().length() > CrmConstants.EmployeeRegistration.OPERATING_SYSTEM_MAX_LENGTH) {
            return lang.employeeRegistrationOperatingSystemExceed(CrmConstants.EmployeeRegistration.OPERATING_SYSTEM_MAX_LENGTH);
        }

        if (registration.getAdditionalSoft().length() > CrmConstants.EmployeeRegistration.ADDITIONAL_SOFT_MAX_LENGTH) {
            return lang.employeeRegistrationAdditionalSoftLengthExceed(CrmConstants.EmployeeRegistration.ADDITIONAL_SOFT_MAX_LENGTH);
        }

        if (registration.getResourceComment().length() > CrmConstants.EmployeeRegistration.RESOURCE_COMMENT_MAX_LENGTH) {
            return lang.employeeRegistrationResourceCommentLengthExceed(CrmConstants.EmployeeRegistration.RESOURCE_COMMENT_MAX_LENGTH);
        }

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


    private EmployeeRegistration fillDto(EmployeeRegistration employeeRegistration) {
        employeeRegistration.setEmployeeFullName(view.fullName().getValue());
        employeeRegistration.setComment(view.comment().getValue());
        employeeRegistration.setWorkplace(view.workplace().getValue());
        employeeRegistration.setPosition(view.position().getValue());
        employeeRegistration.setEmploymentDate(view.employmentDate().getValue());
        employeeRegistration.setHeadOfDepartmentId(view.headOfDepartment().getValue() == null ? null : view.headOfDepartment().getValue().getId());
        employeeRegistration.setEquipmentList(view.equipmentList().getValue());
        employeeRegistration.setResourceList(view.resourcesList().getValue());
        employeeRegistration.setPhoneOfficeTypeList(view.phoneOfficeTypeList().getValue());
        employeeRegistration.setWithRegistration(view.withRegistration().getValue());
        employeeRegistration.setEmploymentType(view.employmentType().getValue());
        employeeRegistration.setProbationPeriodMonth( view.probationPeriod().getValue() );
        employeeRegistration.setResourceComment( view.resourceComment().getValue() );
        employeeRegistration.setOperatingSystem( view.operatingSystem().getValue() );
        employeeRegistration.setAdditionalSoft(createAdditionalSoft(view.additionalSoft().getValue(), view.ide().getValue()));
        employeeRegistration.setCuratorsIds( toSet( view.curators().getValue(), PersonShortView::getId ));
        employeeRegistration.setCompanyId(view.company().getValue() == null ? null : view.company().getValue().getId());
        employeeRegistration.setDepartmentId(view.department().getValue() == null ? null : view.department().getValue().getId());

        return employeeRegistration;
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
        view.ide().setValue(false);
        view.employmentDate().setValue(new Date());
        view.headOfDepartment().setValue(null);
        view.department().setValue(null);
        view.equipmentList().setValue(new HashSet<>());
        view.phoneOfficeTypeList().setValue(new HashSet<>());
        setPhoneEquipmentEnabled(false);
        HashSet<En_InternalResource> resources = new HashSet<>();
        resources.add(En_InternalResource.EMAIL);
        view.resourcesList().setValue(resources);
        view.withRegistration().setValue(true);
        view.employmentType().setValue(En_EmploymentType.FULL_TIME);

        view.positionValidation().setValid(true);
        view.fullNameValidation().setValid(true);
        view.headOfDepartmentValidation().setValid(true);
        view.setEmploymentDateValid(true);
        view.curators().setValue( null );

        view.setPositionErrorLabel(lang.employeeRegistrationPositionExceed(CrmConstants.EmployeeRegistration.POSITION_MAX_LENGTH));
        view.setWorkplaceErrorLabel(lang.employeeRegistrationWorkplaceExceed(CrmConstants.EmployeeRegistration.WORKPLACE_MAX_LENGTH));
        view.setOperatingSystemErrorLabel(lang.employeeRegistrationOperatingSystemExceed(CrmConstants.EmployeeRegistration.OPERATING_SYSTEM_MAX_LENGTH));
        view.setAdditionalSoftErrorLabel(lang.employeeRegistrationAdditionalSoftLengthExceed(CrmConstants.EmployeeRegistration.ADDITIONAL_SOFT_MAX_LENGTH));
        view.setResourceCommentErrorLabel(lang.employeeRegistrationResourceCommentLengthExceed(CrmConstants.EmployeeRegistration.RESOURCE_COMMENT_MAX_LENGTH));

        view.company().setValue(null);

        view.departmentEnabled().setEnabled(false);
        view.saveEnabled().setEnabled(true);
    }

    private void setPhoneEquipmentEnabled(boolean isEnabled) {
        view.setEquipmentOptionEnabled(En_EmployeeEquipment.TELEPHONE, isEnabled);
        view.setPhoneOptionEnabled(En_PhoneOfficeType.INTERNATIONAL, isEnabled);
        view.setPhoneOptionEnabled(En_PhoneOfficeType.LONG_DISTANCE, isEnabled);
        view.setPhoneOptionEnabled(En_PhoneOfficeType.OFFICE, isEnabled);
    }

    public String createAdditionalSoft(String additionalSoft, Boolean needIDE) {
        if (!needIDE){
            return additionalSoft;
        }
        if (StringUtils.isBlank(additionalSoft)) {
            return ADDITIONAL_SOFT_IDE;
        }
        return ADDITIONAL_SOFT_IDE + ", " + additionalSoft;
    }

    private boolean isSelectProteiOrProteiST(EntityOption value) {
        if (value == null) return false;
        return MAIN_HOME_COMPANY_NAME.equals(value.getDisplayText()) || PROTEI_ST_HOME_COMPANY_NAME.equals(value.getDisplayText());
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractEmployeeRegistrationCreateView view;
    @Inject
    private EmployeeRegistrationControllerAsync employeeRegistrationService;
    @Inject
    private PolicyService policyService;
    @Inject
    private EmployeeDepartmentModel departmentModel;

    public static final String ADDITIONAL_SOFT_IDE = "IDE";
    private AppEvents.InitDetails initDetails;
}
