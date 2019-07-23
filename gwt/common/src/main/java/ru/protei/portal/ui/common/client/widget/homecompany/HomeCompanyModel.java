package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель домашних компаний
 */
public abstract class HomeCompanyModel implements Activity, SelectorModel<EntityOption> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        for (SelectorWithModel<EntityOption> subscriber : subscribers) {
            subscriber.clearOptions();
        }
    }

    @Override
    public void onSelectorLoad( SelectorWithModel<EntityOption> selector ) {
        if ( selector == null ) {
            return;
        }
        if ( selector.getValues() == null || selector.getValues().isEmpty() ) {
            refreshOptions();
        }
    }

    public void subscribe( SelectorWithModel< EntityOption > selector ) {
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
        companyService.getCompanyOptionList(new CompanyQuery(true).onlyVisibleFields(),
                new FluentCallback<List<EntityOption>>()
                        .withSuccess(companies -> {
                            list.clear();
                            list.addAll(companies);

                            notifySubscribers();
                        }));
    }

    @Inject
    private CompanyControllerAsync companyService;

    private List< EntityOption > list = new ArrayList<>();
    private List<SelectorWithModel> subscribers = new ArrayList<>();
}
