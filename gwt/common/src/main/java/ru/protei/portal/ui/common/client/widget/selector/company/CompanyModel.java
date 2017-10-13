package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Модель селектора компаний
 */
public abstract class CompanyModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        notifySubscribers();
    }

    @Event
    public void onCompanyListChanged( CompanyEvents.ChangeModel event ) {
        notifySubscribers();
    }

    public void subscribe(ModelSelector<EntityOption> selector, List<En_CompanyCategory> categories) {
        subscribers.add( selector );
        selectorToCategories.put(selector, categories);
        refreshOptions(selector, categories);
    }

    private void notifySubscribers() {
        for ( ModelSelector< EntityOption > selector : subscribers ) {
            List<En_CompanyCategory> categories = selectorToCategories.get(selector);
            refreshOptions(selector, categories);
        }
    }

    private void refreshOptions(ModelSelector<EntityOption> selector, List<En_CompanyCategory> categories) {
        companyService.getCompanyOptionList(categories, new RequestCallback<List<EntityOption>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List<EntityOption> options ) {
                list.clear();
                list.addAll( options );
                selector.fillOptions( list );
                selector.refreshValue();
            }
        } );
    }

    @Inject
    CompanyServiceAsync companyService;

    @Inject
    Lang lang;

    private List< EntityOption > list = new ArrayList<>();

    Map<ModelSelector< EntityOption >, List<En_CompanyCategory>> selectorToCategories = new HashMap<>();

    List< ModelSelector< EntityOption > > subscribers = new ArrayList<>();

}
