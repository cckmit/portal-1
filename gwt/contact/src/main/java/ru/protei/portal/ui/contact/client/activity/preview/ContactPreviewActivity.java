package ru.protei.portal.ui.contact.client.activity.preview;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.contact.client.service.ContactServiceAsync;

import java.util.stream.Collectors;

/**
 * Активность превью контакта
 */
public abstract class ContactPreviewActivity implements Activity, AbstractContactPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onShow( ContactEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        this.contactId = event.contact.getId();

        fillView( event.contact );
        view.fullScreen().setVisible( true );
        view.preview().setStyleName( "preview" );
    }

    @Event
    public void onShow( ContactEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        this.contactId = event.contactId;

        fillView( contactId );
        view.fullScreen().setVisible( false );
        view.preview().addStyleName( "col-xs-12 col-lg-6" );
    }

    @Override
    public void onFullScreenPreviewClicked () {
        fireEvent( new ContactEvents.ShowFullScreen( contactId ) );
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
        view.setPhone( value.getContactInfo().phoneList.stream().map( p -> (p.phone + " (" + p.comment + ")") ).collect( Collectors.joining( "," ) ) );
        view.setEmail( value.getContactInfo().emailList.stream().map( e -> (e.email + " (" + e.comment + ")") ).collect( Collectors.joining( "," ) ) );
        view.setAddress( value.getAddress() );
        view.setHomeAddress( value.getAddressHome() );
        view.setBirthday( value.getBirthday() != null ? format.format( value.getBirthday() ) : "" );
        view.setGender( value.getGender().getCode() );
        view.setInfo( value.getInfo() );
    }

    private void fillView( Long id ) {

        if (id == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        contactService.getContact( id, new RequestCallback<Person>() {
            @Override
            public void onError ( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess ( Person value ) {
                fireEvent( new AppEvents.InitPanelName( value.getDisplayName() ) );
                fillView( value );
            }
        } );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractContactPreviewView view;

    @Inject
    ContactServiceAsync contactService;

    private Long contactId;
    private AppEvents.InitDetails initDetails;

    DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy");
}
