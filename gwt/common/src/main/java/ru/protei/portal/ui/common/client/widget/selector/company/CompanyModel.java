package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.query.CompanyQuery;
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
import java.util.stream.Collectors;

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

    public void subscribe(ModelSelector<EntityOption> selector, List<En_CompanyCategory> categories, boolean excludeHomeCompany) {
        subscribers.add( selector );
        CompanyQuery query = makeQuery( categories, excludeHomeCompany );
        selectorToQuery.put(selector, query);
        requestOptions(selector, query);
    }

    private void refreshOptions() {
        for ( ModelSelector< EntityOption > selector : subscribers ) {
            requestOptions(selector, selectorToQuery.get(selector));
        }
    }

    private void requestOptions( ModelSelector<EntityOption> selector, CompanyQuery query ) {
        companyService.getCompanyOptionList(query, new RequestCallback<List<EntityOption>>() {
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

    private CompanyQuery makeQuery(List<En_CompanyCategory> categories, boolean excludeHomeCompany) {
        CompanyQuery query = new CompanyQuery();
        if(categories != null) {
            query.setCategoryIds(
                    categories.stream()
                            .map( En_CompanyCategory:: getId )
                            .collect( Collectors.toList() ) );
        }
        query.setExcludeHomeCompanies( excludeHomeCompany );
        return query;
    }

    @Inject
    CompanyServiceAsync companyService;

    @Inject
    Lang lang;

    private List< EntityOption > list = new ArrayList<>();
    private Map<ModelSelector< EntityOption >, CompanyQuery> selectorToQuery = new HashMap<>();
    private List< ModelSelector< EntityOption > > subscribers = new ArrayList<>();

}
