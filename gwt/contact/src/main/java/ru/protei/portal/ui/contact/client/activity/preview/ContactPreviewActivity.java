package ru.protei.portal.ui.contact.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Активность превью контакта
 */
public abstract class ContactPreviewActivity implements Activity, AbstractContactPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( ContactEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.contact );
    }


    private void fillView( Person value ) {
        view.setLastName( value.getLastName() );
        view.setFirstName( value.getFirstName() );
        view.setSecondName( value.getSecondName() );
        view.setDisplayName( value.getDisplayName() );
        view.setShortName( value.getDisplayShortName() );
        view.setCompany( value.getCompany().getCname() );
        view.setPosition( value.getPosition() );
        view.setDepartment( value.getDepartment() );
        view.setWorkPhone( value.getWorkPhone() );
        view.setPersonalPhone ( value.getHomePhone() );
        view.setWorkFax( value.getFax() );
        view.setPersonalFax( value.getFaxHome() );
        view.setWorkEmail( value.getEmail() );
        view.setPersonalEmail( value.getEmail_own() );
        view.setWorkAddress( value.getAddress() );
        view.setPersonalAddress( value.getAddressHome() );
        view.setBirthday( value.getBirthday().toString() );
        view.setGender( value.getGender().getCode() );
        view.setInfo( value.getInfo() );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractContactPreviewView view;
}
