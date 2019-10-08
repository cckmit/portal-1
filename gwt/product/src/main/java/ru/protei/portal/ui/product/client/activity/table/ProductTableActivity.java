package ru.protei.portal.ui.product.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterActivity;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Активность таблицы продуктов
 */
public abstract class ProductTableActivity implements
        Activity, AbstractPagerActivity, AbstractProductFilterActivity, AbstractProductTableActivity {

    @PostConstruct
    public void init() {
        view.setActivity( this );
        view.setAnimation( animation );
        filterView.setActivity( this );
        pagerView.setActivity( this );

        view.getFilterContainer().add(filterView.asWidget());

        query = makeQuery();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( ProductEvents.Show event ) {
        fireEvent(new ActionBarEvents.Clear());
        if(policyService.hasPrivilegeFor( En_Privilege.PRODUCT_CREATE )){
            fireEvent(new ActionBarEvents.Add( lang.buttonCreate(), null, UiConstants.ActionBarIdentity.PRODUCT ));
        }

        init.parent.clear();
        init.parent.add( view.asWidget() );

        view.getPagerContainer().add( pagerView.asWidget() );

        loadTable();
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !(UiConstants.ActionBarIdentity.PRODUCT.equals( event.identity )) ) {
            return;
        }

        fireEvent(new ProductEvents.Edit(null));
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        loadTable();
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onItemClicked(DevUnit value) {
        showPreview(value);
    }

    @Override
    public void onEditClicked(DevUnit value) {
        if (!value.isDeprecatedUnit()) {
            fireEvent( new ProductEvents.Edit ( value.getId() ));
        }
    }

    @Override
    public void onArchiveClicked(DevUnit value) {
        productService.updateState(value.getId(), value.getState() == En_DevUnitState.DEPRECATED ? En_DevUnitState.ACTIVE : En_DevUnitState.DEPRECATED,
                new FluentCallback<Boolean>()
                        .withSuccess(result -> {
                            loadTable();
                            fireEvent(new NotifyEvents.Show(lang.msgStatusChanged(), NotifyEvents.NotifyType.SUCCESS));
                            fireEvent(new ProductEvents.ProductListChanged());
                        })
        );
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<DevUnit>> asyncCallback ) {
        boolean isFirstChunk = offset == 0;
        query.setOffset(offset);
        query.setLimit(limit);
        productService.getProductList(query, new FluentCallback<SearchResult<DevUnit>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    asyncCallback.onSuccess(sr.getResults());
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                    }
                }));
    }

    private ProductQuery makeQuery() {
        ProductQuery pq = new ProductQuery();
        pq.setSearchString(filterView.searchPattern().getValue());
        pq.setState(filterView.showDeprecated().getValue() ? null : En_DevUnitState.ACTIVE);
        pq.setSortField(filterView.sortField().getValue());
        pq.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        pq.setTypes(filterView.types().getValue());

        return pq;
    }

    private void showPreview (DevUnit value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new ProductEvents.ShowPreview( view.getPreviewContainer(), value, true ) );
        }
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    @Inject
    AbstractProductTableView view;
    @Inject
    Lang lang;
    @Inject
    TableAnimation animation;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    ProductControllerAsync productService;
    @Inject
    AbstractProductFilterView filterView;
    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails init;
    private ProductQuery query;

}
