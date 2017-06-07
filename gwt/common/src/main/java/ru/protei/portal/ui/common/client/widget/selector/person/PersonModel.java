package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.PersonEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель person
 */
public abstract class PersonModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onPersonListChanged1( PersonEvents.ChangeEmployeeModel event ) {
        refreshOptions();
    }

    @Event
    public void onPersonListChanged2( PersonEvents.ChangePersonModel event ) {
        refreshOptions();
    }

    public void subscribe( ModelSelector< PersonShortView > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< PersonShortView > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        personService.getPersonViewList( new RequestCallback< List< PersonShortView > >() {
                    @Override
                    public void onError( Throwable throwable ) {}

                    @Override
                    public void onSuccess( List< PersonShortView > viewList ) {
                        list.clear();
                        list.addAll( viewList );
                        notifySubscribers();
                    }
                } );
    }

    @Inject
    PersonServiceAsync personService;

    @Inject
    Lang lang;

    private List< PersonShortView > list = new ArrayList<>();

    List< ModelSelector< PersonShortView > > subscribers = new ArrayList<>();
}
