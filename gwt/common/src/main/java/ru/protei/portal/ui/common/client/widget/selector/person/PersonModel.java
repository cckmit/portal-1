package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
import java.util.function.Consumer;

/**
 * Модель person
 */
public abstract class PersonModel implements Activity {

    public void requestPersonList( Company company, Consumer< List< PersonShortView > > fillOptionsAction ){
        isPushing = true;
        PersonQuery query = new PersonQuery( company.getId(), true, false, null, En_SortField.person_full_name, En_SortDir.ASC );
        personService.getPersonViewList( query, new RequestCallback< List<PersonShortView> >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<PersonShortView> options ) {
                fillOptionsAction.accept( options );
                isPushing = false;
            }
        } );
    }

    public boolean isPushing(){
        return isPushing;
    }

    @Inject
    PersonServiceAsync personService;

    @Inject
    Lang lang;

    private boolean isPushing;
}
