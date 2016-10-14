package ru.protei.portal.ui.company.client.widget.companycategorybtngroup;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.company.client.service.CompanyServiceAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by turik on 13.10.16.
 */
public abstract class CompanyCategoryModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    public void subscribe( CompanyCategoryBtnGroup btnGroup ) {
        subscribers.add( btnGroup );
        btnGroup.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        companyService.getCompanyCategories("", new RequestCallback< List < CompanyCategory > >() {
            @Override
            public void onError( Throwable throwable ) {}

            @Override
            public void onSuccess( List< CompanyCategory > categories ) {
                list.clear();
                list.addAll( categories );

                notifySubscribers();
            }
        });

    }

    @Inject
    CompanyServiceAsync companyService;

    private List< CompanyCategory > list = new ArrayList< CompanyCategory >();

    List< ModelSelector > subscribers = new ArrayList< ModelSelector >();

}
