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
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Модель селектора компаний
 */
public abstract class CompanyParentModel implements Activity, SelectorModel<EntityOption> {
    @Event
    public void onInit( AuthEvents.Success event ) {
//        refreshHomeCompanies();
        for (SelectorWithModel<EntityOption> subscriber : subscribers) {
            subscriber.clearOptions();
        }
    }

    @Event
    public void onCompanyListChanged( CompanyEvents.ChangeModel event ) {
//        refreshHomeCompanies();
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
            requestOptions(selector, makeQuery( categories) );
        }
    }


    @Override
    public void onSelectorUnload( SelectorWithModel<EntityOption> selector ) {
        if ( selector == null ) {
            return;
        }
        selector.clearOptions();
    }

//    public void subscribe( SelectorWithModel<EntityOption> selector, CompanyQuery query ) {
//        subscribers.add( selector );
//        selectorToQuery.put(selector, query);
//    }

    private void requestOptions( SelectorWithModel<EntityOption> selector, CompanyQuery query ) {
        int stop = 0;
        companyService.getCompanyOptionList(query, new RequestCallback<List<EntityOption>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List<EntityOption> options ) {
                String tp_ = options.stream().filter( o -> o.getDisplayText().contains( "tp_" ) ).map( o -> o.getDisplayText() ).collect( Collectors.joining( ", " ) );
                log.config( "onSuccess(): query: " + query + " tp: " +tp_);//TODO DEBUG
//                putHomeCompaniesAtTheTop( options );
                selector.fillOptions( options );
                selector.refreshValue();
            }
        } );
    }

    private static final Logger log = Logger.getLogger( CompanyParentModel.class.getName() );

    public CompanyQuery makeQuery( List<En_CompanyCategory> categories ) {
        CompanyQuery query = new CompanyQuery();
        if(categories != null) {
            query.setCategoryIds(
                    categories.stream()
                            .map( En_CompanyCategory:: getId )
                            .collect( Collectors.toList() ) );
        }
        query.setParentIdIsNull( true );
        return query;
    }

//    private void refreshHomeCompanies() {
//        companyService.getCompanyOptionList(
//                makeQuery(Collections.singletonList(En_CompanyCategory.HOME)),
//                new RequestCallback<List<EntityOption>>() {
//                    @Override
//                    public void onError(Throwable throwable) {
//                        homeCompanies.clear();
//                    }
//                    @Override
//                    public void onSuccess(List<EntityOption> result) {
//                        homeCompanies.clear();
//                        homeCompanies.addAll(result);
//                    }
//                }
//        );
//    }
//
//    private void putHomeCompaniesAtTheTop(List<EntityOption> companies) {
//        homeCompanies.forEach(homeCompany -> {
//            int value = companies.indexOf(homeCompany);
//            if (value > 0) {
//                companies.add(0, companies.remove(value));
//            }
//        });
//    }

    @Inject
    CompanyControllerAsync companyService;

    @Inject
    Lang lang;

    protected List<En_CompanyCategory> categories = Arrays.asList(
            En_CompanyCategory.CUSTOMER,
            En_CompanyCategory.PARTNER,
            En_CompanyCategory.SUBCONTRACTOR,
            En_CompanyCategory.HOME );

    private Map<SelectorWithModel< EntityOption >, CompanyQuery> selectorToQuery = new HashMap<>();
    private List<SelectorWithModel< EntityOption >> subscribers = new ArrayList<>();
    private List< EntityOption > homeCompanies = new ArrayList<>();
}
