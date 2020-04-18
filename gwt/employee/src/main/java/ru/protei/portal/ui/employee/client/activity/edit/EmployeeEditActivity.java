package ru.protei.portal.ui.employee.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.service.ContactControllerAsync;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

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

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        if (event.id == null) {
            Person newPerson = new Person();
            newPerson.setCompanyId(event.companyId);
            newPerson.setCompany(event.company);
            initialView(newPerson, new WorkerEntry());
        } else {
            contactService.getContact(event.id, new FluentCallback<Person>()
                    .withSuccess(person -> accountService.getContactAccount(person.getId(), new FluentCallback<UserLogin>()
                            .withSuccess(userLogin -> {} /*initialView(person, userLogin == null ? new UserLogin() : userLogin)*/))));
        }
    }

    @Event
    public void onCreateDepartment(CompanyDepartmentEvents.Created event){
        view.setDepartmentCompanyId(view.company().getValue().getId());
    }

    @Event
    public void onChangeDepartment(CompanyDepartmentEvents.Changed event){
        view.setDepartmentCompanyId(view.company().getValue().getId());
    }

    @Event
    public void onRemovedDepartment(CompanyDepartmentEvents.Removed event){
        view.setDepartmentCompanyId(view.company().getValue().getId());
    }

    @Event
    public void onCreatePosition(WorkerPositionEvents.Created event){
        view.setPositionCompanyId(view.company().getValue().getId());
    }

    @Event
    public void onChangePosition(WorkerPositionEvents.Changed event){
        view.setPositionCompanyId(view.company().getValue().getId());
    }

    @Event
    public void onRemovedPosition(WorkerPositionEvents.Removed event){
        view.setPositionCompanyId(view.company().getValue().getId());
    }

    @Override
    public void onAddCompanyDepartmentClicked() {
        CompanyDepartment companyDepartment = new CompanyDepartment();
        companyDepartment.setCompanyId(view.company().getValue().getId());
        fireEvent(new CompanyDepartmentEvents.Edit(companyDepartment));
    }

    @Override
    public void onEditCompanyDepartmentClicked(CompanyDepartment companyDepartment) {
        fireEvent(new CompanyDepartmentEvents.Edit(companyDepartment));
    }

    @Override
    public void onAddWorkerPositionClicked() {
        WorkerPosition workerPosition = new WorkerPosition();
        workerPosition.setCompanyId(view.company().getValue().getId());
        fireEvent(new WorkerPositionEvents.Edit(workerPosition));
    }

    @Override
    public void onEditWorkerPositionClicked(WorkerPosition workerPosition) {
        fireEvent(new WorkerPositionEvents.Edit(workerPosition));
    }

    @Override
    public void onChangedCompanyDepartment(Long departmentId) {
        worker.setDepartmentId(departmentId);
    }

    @Override
    public void onChangedWorkerPosition(Long positionId) {
        worker.setPositionId(positionId);
    }

    @Override
    public void onSaveClicked() {
        String errorMsg = validate();

        if (errorMsg != null) {
            fireErrorMessage(errorMsg);
            return;
        }

        employeeService.createEmployeePerson(applyChangesEmployee(), new FluentCallback<Person>()
                .withSuccess(person -> {
                    worker.setPersonId(person.getId());
                    employeeService.createEmployeeWorker(worker, new FluentCallback<WorkerEntry>()
                            .withSuccess(workerEntry -> {
                                fireEvent(new NotifyEvents.Show(lang.contactSaved(), NotifyEvents.NotifyType.SUCCESS));
                                fireEvent(new Back());
                            }));
                }));



       /* contactService.saveContact(applyChangesContact(), new FluentCallback<Person>().withSuccess(person -> {
                    if (userLogin.getId() == null) {
                        userLogin.setPersonId(person.getId());
                        userLogin.setPerson(person);
                        userLogin.setInfo(person.getDisplayName());
                    }

                    contactService.saveAccount(userLogin, sendWelcomeEmailVisibility && sendWelcomeEmail, new RequestCallback<Boolean>() {
                        @Override
                        public void onError(Throwable throwable) {
                            fireErrorMessage(lang.errEditContactLogin());
                            fireEvent(new Back());
                        }

                        @Override
                        public void onSuccess(Boolean result) {
                            fireEvent(new NotifyEvents.Show(lang.contactSaved(), NotifyEvents.NotifyType.SUCCESS));
                            fireEvent(new PersonEvents.PersonCreated(person, origin));
                            fireEvent(isNew(contact) ? new ContactEvents.Show(true) : new Back());
                        }
                    });
                }
        ));*/
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

        fireEvent(new ConfirmDialogEvents.Show(lang.contactFireConfirmMessage(), lang.contactFire(), removeAction(employee.getId())));
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
    public void onCompanySelected() {
        boolean isValid = view.company().getValue() != null;
        view.companyValidator().setValid(isValid);
        view.companyDepartmentEnabled().setEnabled(isValid);
        view.workerPositionEnabled().setEnabled(isValid);

        if (isValid) {
            worker.setCompanyId(view.company().getValue().getId());
            view.setDepartmentCompanyId(view.company().getValue().getId());
            view.setPositionCompanyId(view.company().getValue().getId());
        }
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

    private boolean isNew(Person person) {
        return person.getId() == null;
    }

    private boolean fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
        return false;
    }

    private Person applyChangesEmployee() {
        employee.setGender(view.gender().getValue());
        employee.setCompanyId(view.company().getValue().getId());
        employee.setFirstName(view.firstName().getValue());
        employee.setLastName(view.lastName().getValue());
        employee.setSecondName(view.secondName().getText());
        employee.setBirthday(view.birthDay().getValue());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(employee.getContactInfo());

        infoFacade.setWorkPhone(view.workPhone().getText());
        infoFacade.setMobilePhone(view.mobilePhone().getText());

        infoFacade.setEmail(view.workEmail().getText());
        infoFacade.setEmail_own(view.personalEmail().getText());
        employee.setPosition(view.workerPosition().getText());
        employee.setDepartment(view.companyDepartment().getText());

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

        if (!view.isDepartmentValid()){
            return lang.errFieldsRequired();
        }

        if(!view.isPositionValid()){
            return lang.errFieldsRequired();
        }

        if (!view.workEmail().getText().isEmpty() && !view.workEmailValidator().isValid()) {
            return lang.errorFieldHasInvalidValue(view.workEmailLabel());
        }

        if (!view.personalEmail().getText().isEmpty() && !view.personalEmailValidator().isValid()) {
            return lang.errorFieldHasInvalidValue(view.personalEmailLabel());
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

    private void initialView(Person person, WorkerEntry worker){
        this.employee = person;
        this.worker = worker;
        fillView(employee);
    }

    private void fillView(Person person){
        view.company().setValue(person.getCompany() == null ? null : person.getCompany().toEntityOption());
        view.companyEnabled().setEnabled(person.getId() == null && person.getCompany() == null);
        view.companyValidator().setValid(person.getCompany() != null);
        view.gender().setValue(person.getGender());
        view.firstName().setValue(person.getFirstName());
        view.lastName().setValue(person.getLastName());
        view.secondName().setText(person.getSecondName());
        view.birthDay().setValue(person.getBirthday());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo());

        view.workPhone().setText(infoFacade.getWorkPhone());
        view.mobilePhone().setText(infoFacade.getMobilePhone());

        view.workEmail().setText(infoFacade.getEmail());
        view.personalEmail().setText(infoFacade.getEmail_own());

        view.workerPosition().setText(StringUtils.isEmpty(person.getPosition()) ? "Выберите Бла-бла" : person.getPosition());
        view.companyDepartment().setText(StringUtils.isEmpty(person.getDepartment()) ? "Выберите Бла-бла" : person.getDepartment());

        view.deletedMsgVisibility().setVisible(person.isDeleted());
        view.firedMsgVisibility().setVisible(person.isFired());
        view.fireBtnVisibility().setVisible(person.getId() != null && !person.isFired());

        view.firstNameErrorLabel().setText(lang.contactFieldLengthExceed(view.firstNameLabel(), FIRST_NAME_SIZE));
        view.secondNameErrorLabel().setText(lang.contactFieldLengthExceed(view.secondNameLabel(), SECOND_NAME_SIZE));
        view.lastNameErrorLabel().setText(lang.contactFieldLengthExceed(view.lastNameLabel(), LAST_NAME_SIZE));
    }

    private boolean hasPrivileges(Long personId) {
        if (personId == null && policyService.hasPrivilegeFor(En_Privilege.CONTACT_CREATE)) {
            return true;
        }

        if (personId != null && policyService.hasPrivilegeFor(En_Privilege.CONTACT_EDIT)) {
            return true;
        }

        return false;
    }

    private Runnable removeAction(Long contactId) {
        return () -> contactService.fireContact(contactId, new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    if (result) {
                        fireEvent(new NotifyEvents.Show(lang.contactFired(), NotifyEvents.NotifyType.SUCCESS));
                        fireEvent(new Back());
                    } else {
                        fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                    }
                })
        );
    }

    @Inject
    AbstractEmployeeEditView view;
    @Inject
    Lang lang;
    @Inject
    EmployeeControllerAsync employeeService;

    @Inject
    ContactControllerAsync contactService;
    @Inject
    AccountControllerAsync accountService;
    @Inject
    PolicyService policyService;


    private Person employee;
    private WorkerEntry worker;
    private AppEvents.InitDetails initDetails;
}
