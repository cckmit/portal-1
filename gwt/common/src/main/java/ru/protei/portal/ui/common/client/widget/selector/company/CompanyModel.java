package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель селектора компаний
 */
public abstract class CompanyModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onCompanyListChanged( CompanyEvents.ChangeModel event ) {
        refreshOptions();
    }

    public void subscribe( ModelSelector<EntityOption> selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< EntityOption > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        companyService.getCompanyOptionList( new RequestCallback<List<EntityOption>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List<EntityOption> options ) {
                list.clear();
                list.addAll( options );

                notifySubscribers();
            }
        } );
    }

    @Inject
    CompanyServiceAsync companyService;

    @Inject
    Lang lang;

    private List< EntityOption > list = new ArrayList<>();

    List< ModelSelector< EntityOption > > subscribers = new ArrayList<>();

}