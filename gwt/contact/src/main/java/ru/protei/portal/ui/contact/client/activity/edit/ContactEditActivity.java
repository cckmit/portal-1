package ru.protei.portal.ui.contact.client.activity.edit;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.common.MD5Utils;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

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

        if(event.id == null) {
            this.fireEvent(new AppEvents.InitPanelName(lang.newContact()));
            Person newPerson = new Person();
            newPerson.setCompanyId(event.companyId);
            initialView(newPerson, new UserLogin());
        }
        else {
            contactService.getContact(event.id, new AsyncCallback<Person>() {
                @Override
                public void onFailure(Throwable throwable) {
                    fireErrorMessage(lang.errGetList());
                }

                @Override
                public void onSuccess(Person person) {
                    fireEvent(new AppEvents.InitPanelName( lang.editContactHeader(person.getDisplayName())));
                    contactService.getUserLogin(person.getId(), new RequestCallback<UserLogin>() {
                        @Override
                        public void onError(Throwable throwable) {}

                        @Override
                        public void onSuccess(UserLogin userLogin) {
                            initialView(person, userLogin == null ? new UserLogin() : userLogin);
                        }
                    } );
                }
            });
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validate ()) {
            return;
        }

        UserLogin userLogin = applyChangesLogin();
        if(userLogin.getUlogin() != null && !userLogin.getUlogin().isEmpty() &&
                (userLogin.getUpass() == null || userLogin.getUpass().isEmpty())) {
            fireEvent(new NotifyEvents.Show(lang.contactPasswordNotDefinied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        contactService.saveContact(applyChangesContact(), new AsyncCallback<Person>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireErrorMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Person person) {
                if (userLogin.getId() == null) {
                    userLogin.setPersonId(person.getId());
                    userLogin.setInfo(person.getDisplayName());
                }
                contactService.saveUserLogin(userLogin, new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireErrorMessage(lang.errEditContactLogin());
                        fireEvent(new Back());
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        fireEvent(new Back());
                    }
                } );
            }
        });
    }

    @Override
    public void onChangeContactLogin() {
        String value = view.login().getText().trim();

        //isCompanyNameExists не принимает пустые строки!
        if ( value.isEmpty() ){
            view.setContactLoginStatus(NameStatus.NONE);
            return;
        }

        contactService.isContactLoginUnique(
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

    private void resetValidationStatus(){
        view.setContactLoginStatus(NameStatus.NONE);
    }

    private boolean fireErrorMessage(String msg) {
        fireEvent( new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
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
        contact.setInfo(view.personInfo().getText());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(contact.getContactInfo());

        infoFacade.setWorkPhone(view.workPhone().getText());
        infoFacade.setHomePhone(view.homePhone().getText());

        infoFacade.setEmail(view.workEmail().getText());
//        contact.setEmail_own(view.personalEmail().getText());
        infoFacade.setFactAddress(view.workAddress().getText());
        infoFacade.setHomeAddress(view.homeAddress().getText());
        infoFacade.setFax(view.workFax().getText());
//        contact.setFaxHome(view.homeFax().getText());
        contact.setPosition(view.displayPosition().getText());
        contact.setDepartment(view.displayDepartment().getText());

        return contact;
    }

    private UserLogin applyChangesLogin() {

        if (view.login().getText() == null || view.login().getText().trim().isEmpty()) {
            account.setUlogin(null);
            account.setUpass(null);
        } else {
            account.setUlogin(view.login().getText());
            if (view.password().getText() == null || view.password().getText().trim().isEmpty()) {
                account.setUpass(null);
            } else {
                if (account.getUpass() == null || !account.getUpass().equals(view.password().getText())) {
                    account.setUpass(MD5Utils.getHash( view.password().getText()));
                }
            }
        }
        return account;
    }

    private boolean validate() {
        return view.companyValidator().isValid() &&
                view.firstNameValidator().isValid() &&
                view.lastNameValidator().isValid() &&
                view.isValidLogin();
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void initialView(Person person, UserLogin userLogin){
        this.contact = person;
        this.account = userLogin;
        fillView(contact, account);
        resetValidationStatus();
    }


    private void fillView(Person person, UserLogin userLogin){
        view.company().setValue(person.getCompany() == null ? null : person.getCompany().toEntityOption());
//        if (person.getCompanyId() == null)
//            view.company().setValue(null);
//        else {
//            view.company().findAndSelectValue(
//                    company ->  company != null && person.getCompanyId().equals(company.getId()),
//                    true
//            );
//        }

        view.gender().setValue(person.getGender());
        view.firstName().setValue(person.getFirstName());
        view.lastName().setValue(person.getLastName());
        view.secondName().setText(person.getSecondName());
        view.displayName().setText(person.getDisplayName());
        view.shortName().setText(person.getDisplayShortName());
        view.birthDay().setValue(person.getBirthday());

        ((IsWidget)view.personInfo()).asWidget().getElement().setInnerHTML(person.getInfo());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo());

        view.workPhone().setText(infoFacade.getWorkPhone());
        view.homePhone().setText(infoFacade.getHomePhone());

        view.workEmail().setText(infoFacade.getEmail());
//        view.personalEmail().setText(person.getEmail_own());
        view.workAddress().setText(infoFacade.getFactAddress());
        view.homeAddress().setText(infoFacade.getHomeAddress());

        view.workFax().setText(infoFacade.getFax());
//        view.homeFax().setText(person.getFaxHome());
        view.displayPosition().setText(person.getPosition());
        view.displayDepartment().setText(person.getDepartment());

        view.login().setText( userLogin.getUlogin() );
        view.password().setText( userLogin.getUpass() );

        view.setEnabledAccount( userLogin.getUlogin() == null && userLogin.getUpass() == null );
    }


    @Inject
    AbstractContactEditView view;

    @Inject
    Lang lang;

    Person contact;
    UserLogin account;

    @Inject
    ContactServiceAsync contactService;


    private AppEvents.InitDetails initDetails;

}
