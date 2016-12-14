package ru.protei.portal.ui.company.client.widget.category;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by turik on 13.10.16.
 */
public abstract class CategoryModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    public void subscribe( ModelSelector< EntityOption > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        companyService.getCategoryOptionList( new RequestCallback< List< EntityOption > >() {
            @Override
            public void onError( Throwable throwable ) {
            }

            @Override
            public void onSuccess( List< EntityOption > categories ) {
                list.clear();
                list.addAll( categories );

                notifySubscribers();
            }
        } );

    }

    @Inject
    CompanyServiceAsync companyService;

    private List< EntityOption > list = new ArrayList<>();

    List< ModelSelector > subscribers = new ArrayList< ModelSelector >();

}
