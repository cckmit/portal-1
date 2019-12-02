package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.util.TransliterationUtils;
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
import java.util.stream.Collectors;

/**
 * Модель селектора компаний
 */
public abstract class CompanyModel implements Activity, SelectorModel<EntityOption> {
    @Event
    public void onInit( AuthEvents.Success event ) {
        for (SelectorWithModel<EntityOption> subscriber : subscribers) {
            subscriber.clearOptions();
        }
    }

    @Event
    public void onCompanyListChanged( CompanyEvents.ChangeModel event ) {
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
            requestOptions(selector, selectorToQuery.get(selector));
        }
    }

    @Override
    public void onSelectorUnload( SelectorWithModel<EntityOption> selector ) {
        if ( selector == null ) {
            return;
        }
        selector.clearOptions();
    }

    public void subscribe( SelectorWithModel<EntityOption> selector, List<En_CompanyCategory> categories ) {
        subscribers.add( selector );
        updateQuery( selector, categories );
    }

    public void updateQuery( SelectorWithModel<EntityOption> selector, List<En_CompanyCategory> categories ) {
        CompanyQuery query = makeQuery( categories, false );
        selectorToQuery.put(selector, query);
    }

    public void updateQuery( SelectorWithModel<EntityOption> selector, List<En_CompanyCategory> categories, boolean isOnlyParentCompanies ) {
        CompanyQuery query = makeQuery( categories, isOnlyParentCompanies );
        selectorToQuery.put(selector, query);
    }

    public void updateQuery(SelectorWithModel<EntityOption> selector, Boolean isShowDeprecated) {
        if (selectorToQuery.get(selector) != null) {
            selectorToQuery.get(selector).setShowDeprecated(isShowDeprecated);
        }
    }

    private void requestOptions( SelectorWithModel<EntityOption> selector, CompanyQuery query ) {
        companyService.getCompanyOptionList(query, new RequestCallback<List<EntityOption>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List<EntityOption> options ) {
                transliteration(options);
                selector.fillOptions( options );
                selector.refreshValue();
            }
        } );
    }

    public CompanyQuery makeQuery( List<En_CompanyCategory> categories, boolean isParentIdIsNull ) {
        CompanyQuery query = new CompanyQuery();
        if(categories != null) {
            query.setCategoryIds(
                    categories.stream()
                            .map( En_CompanyCategory:: getId )
                            .collect( Collectors.toList() ) );
        }
        query.setOnlyParentCompanies( isParentIdIsNull );
        query.setSortHomeCompaniesAtBegin( true );
        return query;
    }

    private void transliteration(List<EntityOption> options) {
        options.forEach(option -> option.setDisplayText(TransliterationUtils.transliterate(option.getDisplayText(), LocaleInfo.getCurrentLocale().getLocaleName())));
    }

    @Inject
    CompanyControllerAsync companyService;

    @Inject
    Lang lang;

    private Map<SelectorWithModel< EntityOption >, CompanyQuery> selectorToQuery = new HashMap<>();
    private List<SelectorWithModel< EntityOption >> subscribers = new ArrayList<>();
}
