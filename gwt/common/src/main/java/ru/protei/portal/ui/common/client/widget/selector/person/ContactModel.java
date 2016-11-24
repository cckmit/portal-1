package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.ContactShortView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
import java.util.function.Consumer;

/**
 * Модель сотрудников любой компании
 */
public abstract class ContactModel implements Activity {

    public void requestPersonList( Company company, Boolean fired, Consumer< List< ContactShortView > > fillOptionsAction ){
        isPushing = true;
        ContactQuery query = new ContactQuery( company.getId(), fired, null, En_SortField.person_full_name, En_SortDir.ASC );
        contactService.getContactViewList( query, new RequestCallback< List< ContactShortView > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<ContactShortView> options ) {
                fillOptionsAction.accept( options );
                isPushing = false;
            }
        } );
    }

    public boolean isPushing(){
        return isPushing;
    }

    @Inject
    ContactServiceAsync contactService;

    @Inject
    Lang lang;

    private boolean isPushing;
}
