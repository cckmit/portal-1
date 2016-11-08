package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by turik on 28.10.16.
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

    public void subscribe( ModelSelector< Company > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< Company > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        companyService.getCompanies(
                new CompanyQuery( null, En_SortField.comp_name, En_SortDir.ASC),
                new RequestCallback< List< Company > >() {

                    @Override
                    public void onError( Throwable throwable ) {
                    }

                    @Override
                    public void onSuccess( List< Company > companies ) {
                        list.clear();
                        list.addAll( companies );

                        notifySubscribers();
                }
        });
    }

    @Inject
    CompanyServiceAsync companyService;

    private List< Company > list = new ArrayList<>();

    List< ModelSelector< Company > > subscribers = new ArrayList<>();

}
