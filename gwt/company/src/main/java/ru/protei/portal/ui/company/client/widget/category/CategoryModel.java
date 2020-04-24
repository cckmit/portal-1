package ru.protei.portal.ui.company.client.widget.category;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;

import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
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

    public void subscribe( SelectorWithModel< En_CompanyCategory > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( SelectorWithModel selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        companyService.getCategoryOptionList( new RequestCallback< List< En_CompanyCategory > >() {
            @Override
            public void onError( Throwable throwable ) {
            }

            @Override
            public void onSuccess( List<En_CompanyCategory> categories ) {
                list.clear();
                list.addAll( categories );

                notifySubscribers();
            }
        } );

    }

    @Inject
    CompanyControllerAsync companyService;

    private List< En_CompanyCategory > list = new ArrayList<>();

    List<SelectorWithModel> subscribers = new ArrayList<SelectorWithModel>();

}
