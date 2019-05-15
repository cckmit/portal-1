package ru.protei.portal.ui.contact.client.activity.edit;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.ContactControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import static ru.protei.portal.core.model.helper.StringUtils.defaultString;

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

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        origin = event.origin;

        if (event.id == null) {
            this.fireEvent(new AppEvents.InitPanelName(lang.newContact()));
            Person newPerson = new Person();
            newPerson.setCompanyId(event.companyId);
            newPerson.setCompany(event.company);
            initialView(newPerson, new UserLogin());
        } else {
            contactService.getContact(event.id, new AsyncCallback<Person>() {
                @Override
                public void onFailure(Throwable throwable) {
                    fireErrorMessage(lang.errGetList());
                }

                @Override
                public void onSuccess(Person person) {
                    accountService.getContactAccount(person.getId(), new RequestCallback<UserLogin>() {
                        @Override
                        public void onError(Throwable throwable) {}

                        @Override
                        public void onSuccess(UserLogin userLogin) {
                            fireEvent(new AppEvents.InitPanelName( lang.editContactHeader(person.getDisplayName())));
                            initialView(person, userLogin == null ? new UserLogin() : userLogin);
                        }
                    } );
                }
            });
        }
    }

    @Event
    public void onConfirmFire( ConfirmDialogEvents.Confirm event ) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        if (contact.getId() == null || contact.isFired()) {
            return;
        }

        contactService.fireContact(contact.getId(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireErrorMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    fireEvent(new NotifyEvents.Show(lang.contactFired(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new Back());
                } else {
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
    }

    @Override
    public void onSaveClicked() {
        if (!validate()) {
            return;
        }

        if(!isConfirmValidate()) {
            fireEvent(new NotifyEvents.Show(lang.accountPasswordsNotMatch(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        UserLogin userLogin = applyChangesLogin();
        if(!HelperFunc.isEmpty(userLogin.getUlogin()) && HelperFunc.isEmpty(userLogin.getUpass()) && userLogin.getId() == null) {
            fireEvent(new NotifyEvents.Show(lang.accountPasswordNotDefinied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        Boolean sendWelcomeEmail = view.sendWelcomeEmail().getValue();

        contactService.saveContact(applyChangesContact(), new AsyncCallback<Person>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireErrorMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Person person) {
                if (userLogin.getId() == null) {
                    userLogin.setPersonId(person.getId());
                    userLogin.setPerson(person);
                    userLogin.setInfo(person.getDisplayName());
                }
                contactService.saveAccount(userLogin, sendWelcomeEmail, new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireErrorMessage(lang.errEditContactLogin());
                        fireEvent(new Back());
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        fireEvent(new Back());
                        fireEvent(new PersonEvents.PersonCreated(person, origin));
                    }
                } );
            }
        });
    }

    @Override
    public void onChangeContactLogin() {
        view.sendWelcomeEmailVisibility().setVisible(true);

        String value = view.login().getText().trim();

        if (value.isEmpty()){
            view.setContactLoginStatus(NameStatus.NONE);
            return;
        }

        accountService.isLoginUnique(
                value,
                account.getId(),
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {}

                    @Override
                    public void onSuccess(Boolean isUnique) {
                        view.setContactLoginStatus(isUnique ? NameStatus.SUCCESS : NameStatus.ERROR);
                    }
                }
        );
    }

    @Override
    public void onChangeContactPassword() {
        view.sendWelcomeEmailVisibility().setVisible(true);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onFireClicked() {

        if (contact.isFired()) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.contactFireConfirmMessage(), lang.contactFire()));
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
        if (!HelperFunc.isEmpty(view.password().getText())) {
            account.setUpass(view.password().getText());
        }
        return account;
    }

    private boolean validate() {
        return view.companyValidator().isValid() &&
                view.firstNameValidator().isValid() &&
                view.lastNameValidator().isValid() &&
                view.isValidLogin();
    }

    private void initialView(Person person, UserLogin userLogin){
        this.contact = person;
        this.account = userLogin;
        fillView(contact, account);
        resetValidationStatus();
    }

    private void fillView(Person person, UserLogin userLogin){
        view.company().setValue(person.getCompany() == null ? null : person.getCompany().toEntityOption());
        // lock company field if provided person already contains defined company. Added with CRM-103 task.
        view.companyEnabled().setEnabled(person.getId() == null && person.getCompany() == null);
        view.gender().setValue(person.getGender());
        view.firstName().setValue(person.getFirstName());
        view.lastName().setValue(person.getLastName());
        view.secondName().setText(person.getSecondName());
        view.displayName().setText(person.getDisplayName());
        view.shortName().setText(person.getDisplayShortName());
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
        view.password().setText("");
        view.confirmPassword().setText("");

        view.deletedMsgVisibility().setVisible(person.isDeleted());
        view.firedMsgVisibility().setVisible(person.isFired());
        view.fireBtnVisibility().setVisible(person.getId() != null && !person.isFired());
        view.sendWelcomeEmailVisibility().setVisible(false);
        view.sendWelcomeEmail().setValue(false);

        view.showInfo(userLogin.getId() != null);
    }

    private boolean isConfirmValidate() {
        return HelperFunc.isEmpty(view.login().getText()) ||
                HelperFunc.isEmpty(view.password().getText()) ||
                (!HelperFunc.isEmpty(view.confirmPassword().getText()) &&
                        view.password().getText().equals(view.confirmPassword().getText()));
    }

    @Inject
    AbstractContactEditView view;
    @Inject
    Lang lang;
    @Inject
    ContactControllerAsync contactService;
    @Inject
    PolicyService policyService;

    @Inject
    AccountControllerAsync accountService;
    @Inject
    CompanyControllerAsync companyService;

    private Person contact;
    private UserLogin account;
    private AppEvents.InitDetails initDetails;
    private String origin;
}
