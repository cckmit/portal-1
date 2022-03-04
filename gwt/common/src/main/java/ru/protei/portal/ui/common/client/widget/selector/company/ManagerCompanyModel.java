package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;

/**
 * Модель селектора компаний менеджера в фильтре на странице обращений
 */
public abstract class ManagerCompanyModel implements Activity, AsyncSelectorModel<EntityOption>, SelectorItemRenderer<EntityOption> {
    @Event
    public void onInit( AuthEvents.Success event ) {
        cache.clearCache();
    }

    @Event
    public void onCompanyListChanged( CompanyEvents.ChangeModel event ) {
        cache.clearCache();
    }


    public ManagerCompanyModel() {
        query = makeQuery();
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
                    transliteration(options);
                    handler.onSuccess(options);
                }
            } );
        };
    }

    private CompanyQuery makeQuery() {
        CompanyQuery query = new CompanyQuery(querySortParameters);
        query.setCategoryIds(categories);
        query.setOnlyParentCompanies( false );
        query.setSortHomeCompaniesAtBegin( true );

        return query;
    }

    private void transliteration(List<EntityOption> options) {
        options.forEach(option -> option.setDisplayText(transliterate(option.getDisplayText())));
    }

    @Inject
    CompanyControllerAsync companyService;

    @Inject
    Lang lang;

    private List<Integer> categories = Arrays.asList(
            En_CompanyCategory.PARTNER.getId(),
            En_CompanyCategory.SUBCONTRACTOR.getId(),
            En_CompanyCategory.HOME.getId());

    private CompanyQuery query;

    private List <Pair<En_SortField, En_SortDir>> querySortParameters = Arrays.asList(
            new Pair<>(En_SortField.category, En_SortDir.DESC),
            new Pair<>(En_SortField.comp_name, En_SortDir.ASC)
    );

    private SelectorDataCache<EntityOption> cache = new SelectorDataCache<>();
}
