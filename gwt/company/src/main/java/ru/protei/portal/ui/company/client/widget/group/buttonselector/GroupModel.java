package ru.protei.portal.ui.company.client.widget.group.buttonselector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CompanyGroupEvents;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель селектора групп компаний
 */
public abstract class GroupModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onGroupListChanged( CompanyGroupEvents.ChangeModel event ) {
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

        companyService.getGroupOptionList( new RequestCallback< List< EntityOption > >() {
            @Override
            public void onError( Throwable throwable ) {

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
    CompanyControllerAsync companyService;

    private List< EntityOption > list = new ArrayList<>();

    List<SelectorWithModel< EntityOption >> subscribers = new ArrayList<>();
}
