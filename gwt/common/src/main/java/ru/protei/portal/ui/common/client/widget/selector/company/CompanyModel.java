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
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Модель селектора компаний
 */
public abstract class CompanyModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshHomeCompanies(this::refreshOptions);
    }

    @Event
    public void onCompanyListChanged( CompanyEvents.ChangeModel event ) {
        refreshHomeCompanies(this::refreshOptions);
    }

    public void subscribe( ModelSelector<EntityOption> selector, List<En_CompanyCategory> categories ) {
        subscribers.add( selector );
        updateQuery( selector, categories );
    }

    public void updateQuery( ModelSelector<EntityOption> selector, List<En_CompanyCategory> categories ) {
        CompanyQuery query = makeQuery( categories );
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
                putHomeCompaniesAtTheTop( options );
                selector.fillOptions( options );
                selector.refreshValue();
            }
        } );
    }

    private CompanyQuery makeQuery( List<En_CompanyCategory> categories ) {
        CompanyQuery query = new CompanyQuery();
        if(categories != null) {
            query.setCategoryIds(
                    categories.stream()
                            .map( En_CompanyCategory:: getId )
                            .collect( Collectors.toList() ) );
        }
        return query;
    }

    private void refreshHomeCompanies(Runnable andThen) {
        companyService.getCompanyOptionList(
                makeQuery(Collections.singletonList(En_CompanyCategory.HOME)),
                new RequestCallback<List<EntityOption>>() {
                    @Override
                    public void onError(Throwable throwable) {
                        homeCompanies.clear();
                        andThen.run();
                    }
                    @Override
                    public void onSuccess(List<EntityOption> result) {
                        homeCompanies.clear();
                        homeCompanies.addAll(result);
                        andThen.run();
                    }
                }
        );
    }

    private void putHomeCompaniesAtTheTop(List<EntityOption> companies) {
        homeCompanies.forEach(homeCompany -> {
            int value = companies.indexOf(homeCompany);
            if (value > 0) {
                companies.add(0, companies.remove(value));
            }
        });
    }

    @Inject
    CompanyControllerAsync companyService;

    @Inject
    Lang lang;

    private Map<ModelSelector< EntityOption >, CompanyQuery> selectorToQuery = new HashMap<>();
    private List< ModelSelector< EntityOption > > subscribers = new ArrayList<>();
    private List< EntityOption > homeCompanies = new ArrayList<>();
}
