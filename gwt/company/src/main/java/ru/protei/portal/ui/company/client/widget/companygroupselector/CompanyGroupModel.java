package ru.protei.portal.ui.company.client.widget.companygroupselector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.CompanyGroupEvents;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.company.client.service.CompanyServiceAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель селектора групп компаний
 */
public abstract class CompanyGroupModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onGroupListChanged( CompanyGroupEvents.ChangeModel event ) {
        refreshOptions();
    }

    public void subscribe( CompanyGroupSelector selector ) {
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

        companyService.getCompanyGroups( "", new RequestCallback< List< CompanyGroup > >() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( List< CompanyGroup > groups ) {
                list.clear();
                list.addAll( groups );

                notifySubscribers();
            }
        });
    }

    @Inject
    CompanyServiceAsync companyService;

    private List< CompanyGroup > list = new ArrayList< CompanyGroup >();

    List< ModelSelector > subscribers = new ArrayList< ModelSelector >();
}
