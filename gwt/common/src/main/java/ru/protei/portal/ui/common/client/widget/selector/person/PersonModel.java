package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Модель person
 */
public abstract class PersonModel implements Activity {

    @Event
    public void onAuthSuccess(AuthEvents.Success event ) {
        myId = event.profile.getId();
    }

    public void requestPersonList( Company company, Boolean fired, Consumer< List< PersonShortView > > fillOptionsAction ){
        isPushing = true;
        Set<Long> companyIds = new HashSet<>();
        companyIds.add(company.getId());
        PersonQuery query = new PersonQuery( companyIds, null, fired, false, null, En_SortField.person_full_name, En_SortDir.ASC );
        personService.getPersonViewList( query, new RequestCallback< List<PersonShortView> >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<PersonShortView> options ) {
                int value = options.indexOf( new PersonShortView("", myId, false ) );
                if ( value > 0 ) {
                    options.add(0, options.remove(value));
                }

                fillOptionsAction.accept( options );
                isPushing = false;
            }
        } );
    }

    public boolean isPushing(){
        return isPushing;
    }

    @Inject
    PersonControllerAsync personService;

    @Inject
    Lang lang;

    Long myId;

    private boolean isPushing;
}
