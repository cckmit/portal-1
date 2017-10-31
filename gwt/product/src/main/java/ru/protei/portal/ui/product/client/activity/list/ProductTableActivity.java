package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.List;

/**
 * Активность таблицы продуктов
 */
public abstract class ProductTableActivity extends ProductGridActivity implements
        AbstractPagerActivity, ClickColumn.Handler<DevUnit>, EditClickColumn.EditHandler< DevUnit >,
        InfiniteLoadHandler<DevUnit>, InfiniteTableWidget.PagerListener {

    @PostConstruct
    public void init() {
        view.setActivity( this );
        view.setAnimation( animation );
        pagerView.setPageSize( view.getPageSize() );
        pagerView.setActivity( this );
    }

    @Event
    public void onShow( ProductEvents.Show event ) {
        if(filterView.viewType().getValue() != ViewType.TABLE)
            return;

        init(this::requestProductsCount, view.asWidget(), pagerView.asWidget());
        view.getFilterContainer().add(filterView.asWidget());
        requestProductsCount();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !(UiConstants.ActionBarIdentity.PRODUCT.equals( event.identity ) && filterView.viewType().getValue() == ViewType.TABLE) ) {
            return;
        }

        fireEvent(new ProductEvents.Edit(null));
    }

    @Event
    public void onChangeRow( ProductEvents.ChangeProduct event ) {
        productService.getProduct( event.productId, new RequestCallback<DevUnit>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( DevUnit product ) {
                view.updateRow(product);
            }
        } );
    }

    @Override
    public void onFirstClicked() {
        view.scrollTo( 0 );
    }

    @Override
    public void onLastClicked() {
        view.scrollTo( view.getPageCount()-1 );
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage( page + 1 );
    }

    @Override
    public void onItemClicked(DevUnit value) {
        showPreview(value);
    }

    @Override
    public void onEditClicked(DevUnit value) {
        fireEvent( new ProductEvents.Edit ( value.getId() ));
    }

    private void showPreview (DevUnit value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new ProductEvents.ShowPreview( view.getPreviewContainer(), value, true ) );
        }
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<DevUnit>> asyncCallback ) {
        query.setOffset( offset );
        query.setLimit( limit );

        productService.getProductList(query, new RequestCallback< List <DevUnit> >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List< DevUnit > products ) {
                asyncCallback.onSuccess( products );
            }
        });

    }

    private void requestProductsCount() {
        view.clearRecords();
        animation.closeDetails();

        productService.getProductsCount(query, new RequestCallback< Long >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( Long count ) {
                view.setIssuesCount( count );
                pagerView.setTotalPages( view.getPageCount() );
            }
        });
    }

    @Inject
    AbstractProductTableView view;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractPagerView pagerView;

}
