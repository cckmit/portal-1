package ru.protei.portal.ui.employee.client.activity.edit;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_FileUploadStatus;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.ent.WorkerPosition;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.struct.UploadResult;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AvatarUtils;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.util.CrmConstants.ContactConstants.*;

/**
 * Активность создания и редактирования сотрудников
 */
public abstract class EmployeeEditActivity implements AbstractEmployeeEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( EmployeeEvents.Edit event ) {
        if (!hasPrivileges(event.id)) {
            fireEvent(new ForbiddenEvents.Show(initDetails.parent));
            return;
        }

        setAvatarHandlers();

        personId = null;
        workerId = null;

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        companyService.getCompanyOptionList(new CompanyQuery(true, false).onlyVisibleFields().synchronizeWith1C(false),
                new FluentCallback<List<EntityOption>>()
                        .withSuccess(companies -> {
                            companiesWithoutSync.clear();
                            companiesWithoutSync.addAll(companies);

                            if (event.id == null) {
                                fillView(new EmployeeShortView());
                            } else {
                                personId = event.id;
                                fillView(event.id);
                            }

                        }));
    }

    private void setAvatarHandlers() {
        if (changeAvatarHandlerRegistration != null){
            changeAvatarHandlerRegistration.removeHandler();
        }

        changeAvatarHandlerRegistration = view.addChangeHandler(changeEvent -> {
            if (personId != null){
                view.submitAvatar(AvatarUtils.setAvatarUrl(personId));
            }
        });

        if (submitAvatarHandlerRegistration != null){
            submitAvatarHandlerRegistration.removeHandler();
        }

        submitAvatarHandlerRegistration = view.addSubmitCompleteHandler(submitCompleteEvent -> {
            view.setAvatarUrl(AvatarUtils.getPhotoUrl(personId));

            UploadResult result = parseUploadResult(submitCompleteEvent.getResults());

            if (En_FileUploadStatus.OK.equals(result.getStatus())) {
                fireEvent(new NotifyEvents.Show(lang.employeeAvatarUploadSuccessful(), NotifyEvents.NotifyType.SUCCESS));
            } else {
                fireEvent(new NotifyEvents.Show(lang.employeeAvatarUploadingFailed(), NotifyEvents.NotifyType.ERROR));
            }
        });
    }


    @Event
    public void onCreateDepartment(CompanyDepartmentEvents.Created event){
        view.companyDepartmentSelectorReload();
        view.companyDepartment().setValue(new EntityOption(event.companyDepartment.getName(), event.companyDepartment.getId()));
    }

    @Event
    public void onChangeDepartment(CompanyDepartmentEvents.Changed event){
        view.companyDepartmentSelectorReload();
    }

    @Event
    public void onRemovedDepartment(CompanyDepartmentEvents.Removed event){
        view.companyDepartmentSelectorReload();
    }

    @Event
    public void onCreatePosition(WorkerPositionEvents.Created event){
        view.workerPositionSelectorReload();
        view.workerPosition().setValue(new EntityOption(event.workerPosition.getName(), event.workerPosition.getId()));
    }

    @Event
    public void onChangePosition(WorkerPositionEvents.Changed event){
        view.workerPositionSelectorReload();
    }

    @Event
    public void onRemovedPosition(WorkerPositionEvents.Removed event){
        view.workerPositionSelectorReload();
    }

    @Override
    public void onAddCompanyDepartmentClicked() {
        CompanyDepartment companyDepartment = new CompanyDepartment();
        companyDepartment.setCompanyId(view.company().getValue().getId());
        fireEvent(new CompanyDepartmentEvents.Edit(companyDepartment));
    }

    @Override
    public void onEditCompanyDepartmentClicked(Long id, String text) {
        CompanyDepartment companyDepartment = new CompanyDepartment();
        companyDepartment.setId(id);
        companyDepartment.setName(text);
        companyDepartment.setCompanyId(view.company().getValue().getId());
        fireEvent(new CompanyDepartmentEvents.Edit(companyDepartment));
    }

    @Override
    public void onAddWorkerPositionClicked() {
        WorkerPosition workerPosition = new WorkerPosition();
        workerPosition.setCompanyId(view.company().getValue().getId());
        fireEvent(new WorkerPositionEvents.Edit(workerPosition));
    }

    @Override
    public void onEditWorkerPositionClicked(Long id, String text) {
        WorkerPosition workerPosition = new WorkerPosition();
        workerPosition.setId(id);
        workerPosition.setName(text);
        workerPosition.setCompanyId(view.company().getValue().getId());
        fireEvent(new WorkerPositionEvents.Edit(workerPosition));
    }


    @Override
    public void onSaveClicked() {
        if (employee.isFired()){
            fireEvent(new Back());
            return;
        }

        String errorMsg = validate();

        if (errorMsg != null) {
            fireErrorMessage(errorMsg);
            return;
        }

        WorkerEntry worker = new WorkerEntry();
        worker.setPositionId(view.workerPosition().getValue().getId());
        worker.setDepartmentId(view.companyDepartment().getValue().getId());
        worker.setCompanyId(view.company().getValue().getId());

        if (personId == null) {
            createPersonAndCreateWorker(worker);
            return;
        }

        worker.setPersonId(personId);

        if (workerId == null){
            if (isEditablePerson){
                updatePersonAndCreateWorker(worker);
            } else {
                worker.setActiveFlag(0);
                createEmployeeWorker(worker);
            }
            return;
        }

        worker.setId(workerId);

        if (isEditablePerson){
            updatePersonAndUpdateWorker(worker);
        } else {
            updateEmployeeWorker(worker);
        }

    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onFireClicked() {
        if (employee.isFired()) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.employeeFireConfirmMessage(), lang.employeeFire(), removeAction()));
    }

    @Override
    public void validateLimitedFields() {
        if (view.firstName().getValue() != null) {
            view.firstNameErrorLabelVisibility().setVisible(view.firstName().getValue().length() > FIRST_NAME_SIZE);
        }

        if (view.lastName().getValue() != null) {
            view.lastNameErrorLabelVisibility().setVisible(view.lastName().getValue().length() > LAST_NAME_SIZE);
        }

        if (view.secondName().getText() != null) {
            view.secondNameErrorLabelVisibility().setVisible(view.secondName().getText().length() > SECOND_NAME_SIZE);
        }

        view.saveEnabled().setEnabled(validateSaveButton());
    }

    @Override
    public void checkLastNameChanged() {
        if (personId != null){
            view.changeAccountVisibility().setVisible(!Objects.equals(personLastName, view.lastName().getValue()));
            if (!Objects.equals(personLastName, view.lastName().getValue())) {
                view.changeAccount().setValue(false);
            }
        }
    }

    @Override
    public void onCompanySelected() {
        boolean isValid = view.company().getValue() != null;
        view.companyValidator().setValid(isValid);
        view.setAddButtonCompanyDepartmentVisible(isValid);
        view.setAddButtonWorkerPositionVisible(isValid);

        if (isValid) {
            view.updateCompanyDepartments(view.company().getValue().getId());
            view.updateWorkerPositions(view.company().getValue().getId());
        } else {
            view.updateCompanyDepartments(null);
            view.updateWorkerPositions(null);
        }
    }

    @Override
    public void onGenderSelected() {
        checkGenderValid();
    }

    private boolean validateSaveButton() {
        if ((view.firstName().getValue() != null) && (view.firstName().getValue().length() > FIRST_NAME_SIZE)) {
            return false;
        }

        if ((view.secondName().getText() != null) && (view.secondName().getText().length() > SECOND_NAME_SIZE)) {
            return false;
        }

        if ((view.lastName().getValue() != null) && (view.lastName().getValue().length() > LAST_NAME_SIZE)) {
            return false;
        }

        return true;
    }

    private boolean fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
        return false;
    }

    private Person applyChangesEmployee() {
        employee.setId(personId);
        employee.setGender(view.gender().getValue());
        employee.setFirstName(view.firstName().getValue());
        employee.setLastName(view.lastName().getValue());
        employee.setSecondName(view.secondName().getText());
        employee.setBirthday(view.birthDay().getValue());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(employee.getContactInfo());

        infoFacade.setWorkPhone(view.workPhone().getText());
        infoFacade.setMobilePhone(view.mobilePhone().getText());

        infoFacade.setEmail(view.workEmail().getValue());
        employee.setIpAddress(view.ipAddress().getValue());

        return employee;
    }

    private String validate() {
        if (!view.companyValidator().isValid()) {
            return lang.errFieldsRequired();
        }

        if (!view.firstNameValidator().isValid()) {
            return lang.errFieldsRequired();
        }

        if (!view.lastNameValidator().isValid()) {
            return lang.errFieldsRequired();
        }

        if (!view.companyDepartmentValidator().isValid()){
            return lang.errFieldsRequired();
        }

        if(!view.workerPositionValidator().isValid()){
            return lang.errFieldsRequired();
        }

        if(!view.genderValidator().isValid()){
            return lang.errFieldsRequired();
        }

        if (view.workEmail().getValue() != null && !view.workEmailValidator().isValid()) {
            return lang.errorFieldHasInvalidValue(view.workEmailLabel());
        }

        if (view.ipAddress().getValue() != null && !view.ipAddressValidator().isValid()) {
            return lang.errorFieldHasInvalidValue(view.ipAddressLabel());
        }

        if ((view.firstName().getValue() != null) && (view.firstName().getValue().length() > FIRST_NAME_SIZE)) {
            return lang.errorFieldHasInvalidValue(view.firstNameLabel());
        }

        if ((view.lastName().getValue() != null) && (view.lastName().getValue().length() > LAST_NAME_SIZE)) {
            return lang.errorFieldHasInvalidValue(view.lastNameLabel());
        }

        if ((view.secondName().getText() != null) && (view.secondName().getText().length() > SECOND_NAME_SIZE)) {
            return lang.errorFieldHasInvalidValue(view.secondNameLabel());
        }

        return null;
    }

    private void fillView(Long employeeId) {
        employeeService.getEmployeeShortViewWithChangedHiddenCompanyNames(employeeId, new FluentCallback<EmployeeShortView>().withSuccess(this::fillView));
    }

    private void fillView(EmployeeShortView employee){
        setAvatar(employee.getId());
        this.employee.setFired(employee.isFired());
        view.gender().setValue(employee.getGender());
        checkGenderValid();
        view.firstName().setValue(employee.getFirstName());
        view.lastName().setValue(employee.getLastName());
        personLastName = employee.getLastName();
        view.secondName().setText(employee.getSecondName());
        view.birthDay().setValue(employee.getBirthday());
        view.ipAddress().setValue(employee.getIpAddress());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(employee.getContactInfo());
        view.workPhone().setText(employee.getContactInfo() == null ? null : infoFacade.getWorkPhone());
        view.mobilePhone().setText(employee.getContactInfo() == null ? null : infoFacade.getMobilePhone());
        view.workEmail().setValue(employee.getContactInfo() == null ? null : infoFacade.getEmail());

        view.firedMsgVisibility().setVisible(employee.isFired());
        view.fireBtnVisibility().setVisible(employee.getId() != null && !employee.isFired());
        WorkerEntryShortView workerEntry = getWorkerEntryWithoutSync (employee.getWorkerEntries());
        boolean isEmployeeInSyncCompanies = workerEntry != null;

        if (!isEmployeeInSyncCompanies){
            workerEntry = new WorkerEntryShortView();
            workerId = null;
        } else {
            workerId = workerEntry.getId();
        }

        view.company().setValue(workerEntry.getCompanyId() == null ? null : new EntityOption(workerEntry.getCompanyName(), workerEntry.getCompanyId()));
        onCompanySelected();
        view.companyDepartment().setValue(workerEntry.getDepId() == null ? null : new EntityOption(workerEntry.getDepartmentName(), workerEntry.getDepId()));
        view.workerPosition().setValue(workerEntry.getPersonId() == null ? null : new EntityOption(workerEntry.getPositionName(), workerEntry.getPositionId()));

        view.companyValidator().setValid(workerEntry.getCompanyId() != null);
        view.companyDepartmentValidator().setValid(workerEntry.getDepId() != null);
        view.workerPositionValidator().setValid(workerEntry.getPersonId() != null);


        view.firstNameErrorLabel().setText(lang.contactFieldLengthExceed(view.firstNameLabel(), FIRST_NAME_SIZE));
        view.secondNameErrorLabel().setText(lang.contactFieldLengthExceed(view.secondNameLabel(), SECOND_NAME_SIZE));
        view.lastNameErrorLabel().setText(lang.contactFieldLengthExceed(view.lastNameLabel(), LAST_NAME_SIZE));

        boolean isEnabled = (employee.getWorkerEntries() == null || employee.getWorkerEntries().size() == 0) || (isEmployeeInSyncCompanies && employee.getWorkerEntries().size() == 1);
        view.fireBtnVisibility().setVisible(personId != null & isEnabled & !employee.isFired());

        setPersonFieldsEnabled (isEnabled);
        if (employee.isFired()) {
            setAllFieldsEnabled(false);
        }
        view.changeAccount().setValue(false);
        view.changeAccountVisibility().setVisible(false);
    }

    private void setAvatar(Long employeeId) {
        view.setAvatarUrl(AvatarUtils.getPhotoUrl(employeeId));
        view.setAvatarLabelText(employeeId == null ? lang.employeeAvatarLabelDisabled() : lang.employeeAvatarLabelEnabled());
        view.setFileUploadEnabled(employeeId != null);
    }

    private void setPersonFieldsEnabled (boolean isEnabled) {
        isEditablePerson = isEnabled;
        view.firstNameEnabled().setEnabled(isEnabled);
        view.secondNameEnabled().setEnabled(isEnabled);
        view.lastNameEnabled().setEnabled(isEnabled);
        view.birthDayEnabled().setEnabled(isEnabled);
        view.genderEnabled().setEnabled(isEnabled);
        view.workEmailEnabled().setEnabled(isEnabled);
        view.mobilePhoneEnabled().setEnabled(isEnabled);
        view.workPhoneEnabled().setEnabled(isEnabled);
        view.ipAddressEnabled().setEnabled(isEnabled);
        view.companyEnabled().setEnabled(true);
        view.companyDepartmentEnabled().setEnabled(true);
        view.workerPositionEnabled().setEnabled(true);
    }

    private void setAllFieldsEnabled(boolean isEnabled){
        setPersonFieldsEnabled(isEnabled);
        view.companyEnabled().setEnabled(isEnabled);
        view.companyDepartmentEnabled().setEnabled(isEnabled);
        view.workerPositionEnabled().setEnabled(isEnabled);
    }

    private void createEmployeeWorker (WorkerEntry worker){
        employeeService.createEmployeeWorker(worker, new FluentCallback<WorkerEntry>()
                .withSuccess(workerEntry -> {
                    fireEvent(new NotifyEvents.Show(lang.employeeSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new Back());
                }));
    }

    private void updateEmployeeWorker (WorkerEntry worker){
        employeeService.updateEmployeeWorker(worker, new FluentCallback<Boolean>()
                .withSuccess(workerEntry -> {
                    fireEvent(new NotifyEvents.Show(lang.employeeSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new Back());
                }));
    }

    private void createPersonAndCreateWorker(WorkerEntry worker){
        employeeService.createEmployeePerson(applyChangesEmployee(), new FluentCallback<Person>()
                .withSuccess(person -> {
                    worker.setPersonId(person.getId());
                    createEmployeeWorker(worker);
                }));
    }

    private void updatePersonAndCreateWorker(WorkerEntry worker){
        employeeService.updateEmployeePerson(applyChangesEmployee(), view.changeAccount().getValue(), new FluentCallback<Boolean>()
                .withSuccess(success -> {
                    createEmployeeWorker(worker);
                }));
    }

    private void updatePersonAndUpdateWorker(WorkerEntry worker){
        employeeService.updateEmployeePerson(applyChangesEmployee(), view.changeAccount().getValue(), new FluentCallback<Boolean>()
                .withSuccess(success -> {
                    updateEmployeeWorker(worker);
                }));
    }

    private WorkerEntryShortView getWorkerEntryWithoutSync (List<WorkerEntryShortView> workerEntryShortViews) {
        if (workerEntryShortViews != null){
            for (WorkerEntryShortView workerEntryShortView : workerEntryShortViews) {
                for (EntityOption entityOption : companiesWithoutSync) {
                    if (workerEntryShortView.getCompanyId().equals(entityOption.getId())){
                        return workerEntryShortView;
                    }
                }
            }
        }

        return null;
    }

    private boolean hasPrivileges(Long personId) {
        if (personId == null && policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_CREATE)) {
            return true;
        }

        if (personId != null && policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_EDIT)) {
            return true;
        }

        return false;
    }

    private Runnable removeAction() {
        return () -> employeeService.fireEmployee(applyChangesEmployee(), new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    if (result) {
                        fireEvent(new NotifyEvents.Show(lang.employeeFired(), NotifyEvents.NotifyType.SUCCESS));
                        fireEvent(new Back());
                    } else {
                        fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                    }
                })
        );
    }

    private void checkGenderValid(){
        boolean isValid = !view.gender().getValue().equals(En_Gender.UNDEFINED);
        view.genderValidator().setValid(isValid);
    }

    private UploadResult parseUploadResult(String json){
        UploadResult result;

        if (json == null || json.isEmpty()) {
            result = new UploadResult(En_FileUploadStatus.PARSE_ERROR, "");
        } else {
            result = new UploadResult();
            try {
                JSONObject jsonObj = JSONParser.parseStrict(json).isObject();
                result.setStatus(En_FileUploadStatus.getStatus(jsonObj.get("status").isString().stringValue()));
                result.setDetails(jsonObj.get("details").isString().stringValue());
            } catch (Exception e){
                result.setStatus(En_FileUploadStatus.PARSE_ERROR);
                result.setDetails(json);
            }
        }

        return result;
    }

    @Inject
    AbstractEmployeeEditView view;
    @Inject
    Lang lang;
    @Inject
    EmployeeControllerAsync employeeService;
    @Inject
    CompanyControllerAsync companyService;
    @Inject
    PolicyService policyService;

private HandlerRegistration changeAvatarHandlerRegistration;
private HandlerRegistration submitAvatarHandlerRegistration;
    private List<EntityOption> companiesWithoutSync = new ArrayList<>();
    private Person employee = new Person();
    private Long personId;
    private String personLastName;
    private boolean isEditablePerson;
    private Long workerId;
    private AppEvents.InitDetails initDetails;
}
