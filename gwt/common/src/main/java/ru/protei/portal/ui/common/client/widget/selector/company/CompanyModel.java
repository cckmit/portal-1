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
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.toList;
import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;

/**
 * Модель селектора компаний
 */
public abstract class CompanyModel implements Activity, AsyncSelectorModel<EntityOption>, SelectorItemRenderer<EntityOption> {
    @Event
    public void onInit( AuthEvents.Success event ) {
        cache.clearCache();
    }

    @Event
    public void onCompanyListChanged( CompanyEvents.ChangeModel event ) {
        cache.clearCache();
    }


    public CompanyModel() {
        query = makeQuery( categories, false );
        cache.setLoadHandler(makeLoadHandler(query));
    }

    @Override
    public EntityOption get( int elementIndex, LoadingHandler loadingHandler ) {
        return cache.get( elementIndex, loadingHandler );
    }

    @Override
    public String getElementName( EntityOption value ) {
        return value == null ? "" : value.getDisplayText();
    }

    public void setCategories( List<En_CompanyCategory> categories ) {
        query.setCategoryIds( toList( categories, En_CompanyCategory::getId ) );
    }

    public void showOnlyParentCompanies( boolean isOnlyParentCompanies ) {
        query.setOnlyParentCompanies( isOnlyParentCompanies );
    }

    public void showDeprecated( Boolean isShowDeprecated) {
        query.setShowDeprecated(isShowDeprecated);
    }

    private SelectorDataCacheLoadHandler<EntityOption> makeLoadHandler( final CompanyQuery query) {
       return ( offset, limit, handler ) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            companyService.getCompanyOptionList( query, new RequestCallback<List<EntityOption>>() {
                @Override
                public void onError( Throwable throwable ) {
                    fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                    handler.onFailure( throwable );
                }

                @Override
                public void onSuccess( List<EntityOption> options ) {
                    transliterate(options);
                    handler.onSuccess(options);
                }
            } );
        };
    }

    private CompanyQuery makeQuery( List<En_CompanyCategory> categories, boolean isParentIdIsNull ) {
        CompanyQuery query = new CompanyQuery();

        if(categories != null) {
            query.setCategoryIds(
                    categories.stream()
                            .map( En_CompanyCategory:: getId )
                            .collect( Collectors.toList() ) );
        }
        query.setOnlyParentCompanies( isParentIdIsNull );
        query.setSortHomeCompaniesAtBegin( true );
        query.setShowDeprecated( false );
        return query;
    }

    private void transliterate(List<EntityOption> options) {
        options.forEach(option -> option.setDisplayText(transliteration(option.getDisplayText())));
    }

    @Inject
    CompanyControllerAsync companyService;

    @Inject
    Lang lang;

    private List<En_CompanyCategory > categories = Arrays.asList(
            En_CompanyCategory.CUSTOMER,
            En_CompanyCategory.PARTNER,
            En_CompanyCategory.SUBCONTRACTOR,
            En_CompanyCategory.HOME);

    private CompanyQuery query;

    private SelectorDataCache<EntityOption> cache = new SelectorDataCache<>();
}
