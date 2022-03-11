package ru.protei.portal.ui.contact.client.activity.edit;

import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.util.GenerationPasswordUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.service.ContactControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.defaultString;
import static ru.protei.portal.core.model.util.CrmConstants.ContactConstants.*;
import static ru.protei.portal.ui.common.client.common.UiConstants.DateTime.MSK_TIME_ZONE_OFFSET_IN_MINUTES;

/**
 * Активность создания и редактирования контактного лица
 */
public abstract class ContactEditActivity implements AbstractContactEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( ContactEvents.Edit event ) {
        if (!hasPrivileges(event.id)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        origin = event.origin;
        this.fireBackEvent =
                event.backEvent == null ?
                () -> fireEvent(new Back()) :
                event.backEvent;

        if (event.id == null) {
            Person newPerson = new Person();
            newPerson.setCompanyId(event.companyId);
            newPerson.setCompany(event.company);
            initialView(newPerson, new UserLogin());
        } else {
            contactService.getContact(event.id, new FluentCallback<Person>()
                    .withSuccess(person -> accountService.getContactAccount(person.getId(), new FluentCallback<List<UserLogin>>()
                            .withSuccess(userLogins -> initialView(person, isEmpty(userLogins) ? new UserLogin() : userLogins.get(0))))));
        }
    }

    @Override
    public void onSaveClicked() {
        String errorMsg = validate();

        if (errorMsg != null) {
            fireErrorMessage(errorMsg);
            return;
        }

        if (passwordNotDefined()) {
            fireEvent(new NotifyEvents.Show(lang.accountPasswordNotDefinied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        if(!passwordConfirmed()) {
            fireEvent(new NotifyEvents.Show(lang.accountPasswordsNotMatch(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        UserLogin userLogin = applyChangesLogin();

        Boolean sendWelcomeEmailVisibility = view.sendWelcomeEmailVisibility().isVisible();
        Boolean sendWelcomeEmail = view.sendWelcomeEmail().getValue();

        contactService.saveContact(applyChangesContact(), new FluentCallback<Person>().withSuccess(person -> {
                    if (userLogin.getId() == null) {
                        userLogin.setPersonId(person.getId());
                        userLogin.setPerson(person);
                        userLogin.setInfo(person.getDisplayName());
                    }

                    contactService.saveAccount(userLogin, sendWelcomeEmailVisibility && sendWelcomeEmail, new RequestCallback<Long>() {
                        @Override
                        public void onError(Throwable throwable) {
                            fireErrorMessage(lang.errEditContactLogin());
                            fireEvent(new Back());
                        }

                        @Override
                        public void onSuccess(Long result) {
                            fireEvent(new NotifyEvents.Show(lang.contactSaved(), NotifyEvents.NotifyType.SUCCESS));
                            fireEvent(new PersonEvents.PersonCreated(person, origin));
                            fireBackEvent.run();
                        }
                    });
                }
        ));
    }

    @Override
    public void onChangeContactLoginInfo() {
        view.sendWelcomeEmailVisibility().setVisible(isVisibleSendEmail());
        view.sendEmailWarningVisibility().setVisible(isVisibleSendEmailWarning());

        String login = view.login().getText().trim();

        if (login.isEmpty()) {
            view.setContactLoginStatus(NameStatus.NONE);
            view.loginErrorLabelVisibility().setVisible(false);
            view.saveEnabled().setEnabled(validateSaveButton());
            return;
        }

        validateFields();

        if (login.length() > LOGIN_SIZE) {
            view.setContactLoginStatus(NameStatus.NONE);
            return;
        }

        if (!login.contains("@")) {
            view.setContactLoginStatus(NameStatus.NONE);
            return;
        }

        accountService.isLoginUnique(
                login,
                account.getId(),
                new FluentCallback<Boolean>()
                        .withSuccess(isUnique -> view.setContactLoginStatus(isUnique ? NameStatus.SUCCESS : NameStatus.ERROR)));
    }

    @Override
    public void onCancelClicked() {
        fireBackEvent.run();
    }

    @Override
    public void onFireClicked() {
        if (contact.isFired()) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.contactFireConfirmMessage(), lang.contactFire(), removeAction(contact.getId())));
    }

    @Override
    public void onChangeSendWelcomeEmail() {
        view.sendEmailWarningVisibility().setVisible(isVisibleSendEmailWarning());
    }

    @Override
    public void validateFields() {
        if (view.firstName().getValue() != null) {
            view.firstNameErrorLabelVisibility().setVisible(view.firstName().getValue().length() > FIRST_NAME_SIZE);
        }

        if (view.lastName().getValue() != null) {
            view.lastNameErrorLabelVisibility().setVisible(view.lastName().getValue().length() > LAST_NAME_SIZE);
        }

        if (view.secondName().getText() != null) {
            view.secondNameErrorLabelVisibility().setVisible(view.secondName().getText().length() > SECOND_NAME_SIZE);
        }

        if (view.shortName().getText() != null) {
            view.shortNameErrorLabelVisibility().setVisible(view.shortName().getText().length() > SHORT_NAME_SIZE);
        }

        if (view.login().getText() != null) {
            validateLogin();
        }

        view.saveEnabled().setEnabled(validateSaveButton());
    }

    @Override
    public void onCompanySelected() {
        view.companyValidator().setValid(view.company().getValue() != null);
    }

    @Override
    public void onPasswordGenerationClicked() {
        String password = GenerationPasswordUtils.generate();
        view.password().setValue(password);
        view.confirmPassword().setValue(password);

        view.sendWelcomeEmailVisibility().setVisible(isVisibleSendEmail());
        view.sendEmailWarningVisibility().setVisible(isVisibleSendEmailWarning());
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

        if ((view.shortName().getText() != null) && (view.shortName().getText().length() > SHORT_NAME_SIZE)) {
            return false;
        }

        if ((view.login().getText() != null) && (view.login().getText().trim().length() > LOGIN_SIZE)) {
            return false;
        }

        if ((view.login().getText() != null) && (!view.login().getText().trim().isEmpty()) && (!view.login().getText().contains("@"))) {
            return false;
        }

        return true;
    }

    private void resetValidationStatus(){
        view.setContactLoginStatus(NameStatus.NONE);
    }

    private boolean fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
        return false;
    }

    private Person applyChangesContact() {
        contact.setGender(view.gender().getValue());
        contact.setCompanyId(view.company().getValue().getId());
        contact.setFirstName(view.firstName().getValue());
        contact.setLastName(view.lastName().getValue());
        contact.setSecondName(view.secondName().getText());
        contact.setDisplayName(view.displayName().getText());
        contact.setDisplayShortName(view.shortName().getText());
        contact.setBirthday(view.birthDay().getValue());
        contact.setLocale(view.locale().getValue());
        contact.setInfo(view.personInfo().getText());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(contact.getContactInfo());

        infoFacade.setWorkPhone(view.workPhone().getText());
        infoFacade.setHomePhone(view.homePhone().getText());
        infoFacade.setMobilePhone(view.mobilePhone().getText());

        infoFacade.setEmail(view.workEmail().getText());
        infoFacade.setEmail_own(view.personalEmail().getText());
        infoFacade.setFactAddress(view.workAddress().getText());
        infoFacade.setHomeAddress(view.homeAddress().getText());
        infoFacade.setFax(view.workFax().getText());
        infoFacade.setFaxHome(view.homeFax().getText());
        contact.setPosition(view.displayPosition().getText());
        contact.setDepartment(view.displayDepartment().getText());

        return contact;
    }

    private UserLogin applyChangesLogin() {
        account.setUlogin(view.login().getText());
        if (!HelperFunc.isEmpty(view.password().getValue())) {
            account.setUpass(view.password().getValue());
        }
        return account;
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

        if (!isLoginValid()) {
            return lang.errorFieldHasInvalidValue(view.loginLabel());
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

        if ((view.shortName().getText() != null) && (view.shortName().getText().length() > SHORT_NAME_SIZE)) {
            return lang.errorFieldHasInvalidValue(view.shortNameLabel());
        }

        return null;
    }

    private void initialView(Person person, UserLogin userLogin){
        this.contact = person;
        this.account = userLogin;
        fillView(contact, account);
        resetValidationStatus();
        validateFields();
    }

    private void fillView(Person person, UserLogin userLogin){
        view.company().setValue(person.getCompany() == null ? null : person.getCompany().toEntityOption());
        // lock company field if provided person already contains defined company. Added with CRM-103 task.
        view.companyEnabled().setEnabled(person.getId() == null && person.getCompany() == null);
        view.companyValidator().setValid(person.getCompany() != null);
        view.gender().setValue(person.getGender());
        view.firstName().setValue(person.getFirstName());
        view.lastName().setValue(person.getLastName());
        view.secondName().setText(person.getSecondName());
        view.displayName().setText(person.getDisplayName());
        view.shortName().setText(person.getDisplayShortName());
        if (person.getBirthday() != null ) {
            view.setBirthDayTimeZone(TimeZone.createTimeZone(MSK_TIME_ZONE_OFFSET_IN_MINUTES));
        }
        view.birthDay().setValue(person.getBirthday());
        view.locale().setValue(person.getLocale());

        view.personInfo().setText(defaultString(person.getInfo(), ""));

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo());

        view.workPhone().setText(infoFacade.getWorkPhone());
        view.homePhone().setText(infoFacade.getHomePhone());
        view.mobilePhone().setText(infoFacade.getMobilePhone());

        view.workEmail().setText(infoFacade.getEmail());
        view.personalEmail().setText(infoFacade.getEmail_own());
        view.workAddress().setText(infoFacade.getFactAddress());
        view.homeAddress().setText(infoFacade.getHomeAddress());

        view.workFax().setText(infoFacade.getFax());
        view.homeFax().setText(infoFacade.getFaxHome());
        view.displayPosition().setText(person.getPosition());
        view.displayDepartment().setText(person.getDepartment());

        view.login().setText(userLogin.getUlogin());
        view.password().setValue(null);
        view.confirmPassword().setValue(null);

        view.deletedMsgVisibility().setVisible(person.isDeleted());
        view.firedMsgVisibility().setVisible(person.isFired());
        view.fireBtnVisibility().setVisible(person.getId() != null && !person.isFired());
        view.sendWelcomeEmailVisibility().setVisible(false);
        view.sendWelcomeEmail().setValue(true);
        view.sendEmailWarningVisibility().setVisible(false);

        view.showInfo(userLogin.getId() != null);
    }

    private boolean passwordNotDefined() {
        return HelperFunc.isNotEmpty(view.login().getText()) &&
                !Objects.equals(view.login().getText().trim(), account.getUlogin()) &&
                HelperFunc.isEmpty(view.password().getValue());
    }

    private boolean passwordConfirmed() {
        return HelperFunc.isEmpty(view.login().getText()) ||
                HelperFunc.isEmpty(view.password().getValue()) ||
                (!HelperFunc.isEmpty(view.confirmPassword().getValue()) &&
                        view.confirmPassword().getValue().equals(view.password().getValue()));
    }

    private boolean isVisibleSendEmail() {
        return HelperFunc.isNotEmpty(view.login().getText()) &&
                (!Objects.equals(view.login().getText().trim(), account.getUlogin()) ||
                HelperFunc.isNotEmpty(view.password().getValue()));
    }

    private boolean isVisibleSendEmailWarning() {
        return view.sendWelcomeEmailVisibility().isVisible() &&
                view.sendWelcomeEmail().getValue() &&
                view.workEmail().getText().isEmpty() &&
                view.personalEmail().getText().isEmpty();
    }

    private boolean isLoginValid() {
        if (Objects.equals(view.getContactLoginStatus(), NameStatus.ERROR)) {
            return false;
        }

        if ((view.login() != null) && (view.login().getText().trim().length() > LOGIN_SIZE)) {
            return false;
        }

        if ((view.login() != null) && (!view.login().getText().trim().isEmpty()) && (!view.login().getText().contains("@"))) {
            return false;
        }

        return true;
    }

    private void validateLogin() {
        if (view.login().getText().trim().length() > LOGIN_SIZE) {
            view.loginErrorLabel().setText(lang.promptFieldLengthExceed(view.loginLabel(), LOGIN_SIZE));
            view.loginErrorLabelVisibility().setVisible(true);
        } else if (!view.login().getText().trim().isEmpty() && !view.login().getText().contains("@")) {
            view.loginErrorLabel().setText(lang.promptFieldNeedContainAtSign());
            view.loginErrorLabelVisibility().setVisible(true);
        } else {
            view.loginErrorLabelVisibility().setVisible(false);
        }
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
    AbstractContactEditView view;
    @Inject
    Lang lang;
    @Inject
    ContactControllerAsync contactService;
    @Inject
    AccountControllerAsync accountService;
    @Inject
    PolicyService policyService;

    private Person contact;
    private UserLogin account;
    private AppEvents.InitDetails initDetails;
    private String origin;
    private Runnable fireBackEvent = () -> fireEvent(new Back());
}
