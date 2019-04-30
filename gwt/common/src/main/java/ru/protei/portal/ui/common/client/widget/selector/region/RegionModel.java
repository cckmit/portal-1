package ru.protei.portal.ui.common.client.widget.selector.region;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель регионов
 */
public abstract class RegionModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    public void subscribe( SelectorWithModel< EntityOption > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( SelectorWithModel< EntityOption > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        regionService.getRegionList( new RequestCallback< List< EntityOption > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List< EntityOption > options ) {
                list.clear();
                list.addAll( options );
                notifySubscribers();
            }
        } );
    }

    @Inject
    RegionControllerAsync regionService;

    @Inject
    Lang lang;

    private List< EntityOption > list = new ArrayList<>();

    List<SelectorWithModel< EntityOption >> subscribers = new ArrayList<>();
}
