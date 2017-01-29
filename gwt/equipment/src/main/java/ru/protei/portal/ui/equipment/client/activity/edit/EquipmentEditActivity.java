package ru.protei.portal.ui.equipment.client.activity.edit;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;

/**
 * Created by michael on 02.11.16.
 */
public abstract class EquipmentEditActivity implements AbstractEquipmentEditActivity, Activity {

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
            fillView(newPerson);
        }
        else {
            contactService.getContact(event.id, new AsyncCallback<Person>() {
                @Override
                public void onFailure(Throwable throwable) {
                    fireErrorMessage(lang.errGetList());
                }

                @Override
                public void onSuccess(Person person) {
                    fireEvent( new AppEvents.InitPanelName( lang.editContactHeader(person.getDisplayName())));

                    fillView(person);
                }
            });
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validate ()) {
            return;
        }

        contactService.saveContact(applyChanges(), new AsyncCallback<Person>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireErrorMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Person person) {
                fireEvent(new Back());
            }
        });
    }

    private boolean fireErrorMessage(String msg) {
        fireEvent( new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
        return false;
    }

    private Person applyChanges () {
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

    private boolean validate() {
        return view.companyValidator().isValid() &&
                view.firstNameValidator().isValid() &&
                view.lastNameValidator().isValid();
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }


    private void fillView(Person person){
        this.contact = person;
        initDetails.parent.clear();

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

        initDetails.parent.add(view.asWidget());
    }


    @Inject
    AbstractEquipmentEditView view;

    @Inject
    Lang lang;

    Person contact;

    @Inject
    ContactServiceAsync contactService;


    private AppEvents.InitDetails initDetails;

}
