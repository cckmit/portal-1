package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Активность таблицы продуктов
 */
public abstract class ProductTableActivity implements
        Activity, AbstractPagerActivity, AbstractProductTableActivity {

    @PostConstruct
    public void init() {
        view.setActivity( this );
        view.setAnimation( animation );
        pagerView.setActivity( this );
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onShow( ProductEvents.ShowDefinite event ) {
        if(event.viewType != ViewType.TABLE)
            return;

        this.query = event.query;
        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        view.getFilterContainer().add(event.filter);
        loadTable();
    }

    @Event
    public void onFilterChange(ProductEvents.UpdateData event) {
        if (event.viewType != ViewType.TABLE) {
            return;
        }

        this.query = event.query;
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
        if (value.isActiveUnit()) {
            fireEvent( new ProductEvents.Edit ( value.getId() ));
        }
    }

    @Override
    public void onArchiveClicked(DevUnit value) {
        productService.updateState(value.getId(), value.getState() == En_DevUnitState.DEPRECATED ? En_DevUnitState.ACTIVE : En_DevUnitState.DEPRECATED, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onSuccess(Boolean result) {
                loadTable();
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new ProductEvents.ProductListChanged());
            }
        });
    }

    private void showPreview (DevUnit value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new ProductEvents.ShowPreview( view.getPreviewContainer(), value, true, true ) );
        }
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

    private AppEvents.InitDetails init;
    private ProductQuery query;

}
