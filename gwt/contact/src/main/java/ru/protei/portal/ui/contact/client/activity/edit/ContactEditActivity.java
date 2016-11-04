package ru.protei.portal.ui.contact.client.activity.edit;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyService;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.contact.client.service.ContactServiceAsync;

/**
 * Created by michael on 02.11.16.
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

        if(event.id == null) {
            this.fireEvent(new AppEvents.InitPanelName(lang.newContact()));
            Person newPerson = new Person();
            newPerson.setCompanyId(event.companyId);
            setup(newPerson);
        }
        else {
            this.fireEvent( new AppEvents.InitPanelName( "Edit contact with ID: " + event.id ) );
            contactService.getContact(event.id, new AsyncCallback<Person>() {
                @Override
                public void onFailure(Throwable throwable) {
                    fireEvent(new NotifyEvents.Show(lang.errorGetList(), NotifyEvents.NotifyType.ERROR));
                }

                @Override
                public void onSuccess(Person person) {
                    setup(person);
                }
            });
        }
    }

    @Override
    public void onSaveClicked() {
        //
        if (!applyChanges ()) {
            return;
        }

        contactService.saveContact(contact, new AsyncCallback<Person>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent( new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Person person) {
                fireEvent(new Back());
            }
        });
    }

    protected boolean errorMsg (String msg) {
        fireEvent( new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
        return false;
    }

    protected boolean applyChanges () {

        if (view.company().getValue() == null) {
            return errorMsg(lang.errorCompanyRequired());
        }

        if (view.firstName().getText().isEmpty()) {
            return errorMsg(lang.errorFirstNameRequired());
        }

        if (view.lastName().getText().isEmpty()) {
            return errorMsg(lang.errorLastNameRequired());
        }

        contact.setGender(view.gender().getValue());
        contact.setCompanyId(view.company().getValue().getId());
        contact.setFirstName(view.firstName().getText());
        contact.setLastName(view.lastName().getText());
        contact.setSecondName(view.secondName().getText());

        if (view.displayName().getText().isEmpty()) {
            contact.setDisplayName(contact.getLastName() + " " + contact.getFirstName());
        }
        else {
            contact.setDisplayName(view.displayName().getText());
        }

        if (view.shortName().getText().isEmpty()) {
            StringBuilder b = new StringBuilder();
            b.append (contact.getLastName()).append(" ")
            .append (contact.getFirstName().substring(0,1).toUpperCase()).append(".")
            ;

            if (!contact.getSecondName().isEmpty()) {
                b.append(" ").append(contact.getSecondName().substring(0,1).toUpperCase()).append(".");
            }

            contact.setDisplayShortName(b.toString());
        }
        else {
            contact.setDisplayShortName(view.shortName().getText());
        }

        contact.setBirthday(view.birthDay().getValue());

        contact.setInfo(view.personInfo().getText());
        contact.setWorkPhone(view.workPhone().getText());
        contact.setHomePhone(view.homePhone().getText());
        contact.setEmail(view.workEmail().getText());
        contact.setEmail_own(view.personalEmail().getText());
        contact.setAddress(view.workAddress().getText());
        contact.setAddressHome(view.homeAddress().getText());
        contact.setFax(view.workFax().getText());
        contact.setFaxHome(view.homeFax().getText());
        contact.setPosition(view.displayPosition().getText());
        contact.setDepartment(view.displayDepartment().getText());

        return true;
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }


    private void setup (Person person){
        this.contact = person;
        initDetails.parent.clear();

        if (person.getCompanyId() == null)
            view.company().setValue(null);
        else {
            view.company().findAndSelectValue(
                    company ->  company != null && person.getCompanyId().equals(company.getId()),
                    true
            );
        }

        view.gender().setValue(person.getGender());
        view.firstName().setText(person.getFirstName());
        view.lastName().setText(person.getLastName());
        view.secondName().setText(person.getSecondName());
        view.displayName().setText(person.getDisplayName());
        view.shortName().setText(person.getDisplayShortName());
        view.birthDay().setValue(person.getBirthday());

        view.personInfo().setText(person.getInfo());
        view.workPhone().setText(person.getWorkPhone());
        view.homePhone().setText(person.getHomePhone());
        view.workEmail().setText(person.getEmail());
        view.personalEmail().setText(person.getEmail_own());
        view.workAddress().setText(person.getAddress());
        view.homeAddress().setText(person.getHomePhone());
        view.workFax().setText(person.getFax());
        view.homeFax().setText(person.getFaxHome());
        view.displayPosition().setText(person.getPosition());
        view.displayDepartment().setText(person.getDepartment());

        initDetails.parent.add(view.asWidget());
    }


    @Inject
    AbstractContactEditView view;

    @Inject
    Lang lang;

    Person contact;

    @Inject
    ContactServiceAsync contactService;

    @Inject
    CompanyServiceAsync companyService;


    private AppEvents.InitDetails initDetails;

}
