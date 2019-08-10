package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.PlateListAnimation;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemActivity;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;
import ru.protei.portal.ui.product.client.view.item.ProductItemView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Активность списка продуктов
 */
public abstract class ProductListActivity implements Activity, AbstractProductItemActivity, AbstractProductListActivity {

    @PostConstruct
    public void init() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onShow( ProductEvents.ShowDefinite event ) {
        if(event.viewType != ViewType.LIST)
            return;

        this.query = event.query;
        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getFilterContainer().add(event.filter);

        requestProducts();
    }

    @Event
    public void onFilterChange(ProductEvents.UpdateData event) {
        if (event.viewType != ViewType.LIST) {
            return;
        }

        this.query = event.query;
        requestProducts();
    }

    @Override
    public void onFavoriteClicked(AbstractProductItemView itemView) {
        Window.alert( "On favorite clicked" );
    }

    @Override
    public void onPreviewClicked( AbstractProductItemView itemView ) {
        DevUnit value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent( new ProductEvents.ShowPreview( itemView.getPreviewContainer(), value, false, false ) );
        animation.showPreview(itemView, (IsWidget) itemView.getPreviewContainer());
    }

    @Override
    public void onEditClicked( AbstractProductItemView itemView ) {
        fireEvent( new ProductEvents.Edit( itemViewToModel.get( itemView ).getId()  ) );
    }

    @Override
    public void onLockClicked(ProductItemView itemView) {
        DevUnit devUnit = itemViewToModel.get(itemView);
        if (devUnit == null) {
            return;
        }

        productService.updateState(devUnit.getId(), devUnit.getState() == En_DevUnitState.DEPRECATED ? En_DevUnitState.ACTIVE : En_DevUnitState.DEPRECATED, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onSuccess(Boolean result) {
                requestProducts();
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new ProductEvents.ProductListChanged());
            }
        });
    }

    @Override
    public void onCreateClicked( ) { fireEvent(new ProductEvents.Edit()); }

    private AbstractProductItemView makeView ( DevUnit product )
    {
        AbstractProductItemView itemView = factory.get();
        itemView.setName(product.getName());
        itemView.setType(product.getType());
        itemView.setArchived(product.getStateId() > 1);
        itemView.setActivity(this);
        itemView.setEditEnabled( policyService.hasPrivilegeFor( En_Privilege.PRODUCT_EDIT ) );
        itemView.setArchived(!product.isActiveUnit());

        return itemView;
    }

    private void requestProducts() {
        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.getChildContainer().clear();
        view.setListCreateBtnVisible(policyService.hasPrivilegeFor( En_Privilege.PRODUCT_CREATE ));
        itemViewToModel.clear();

        productService.getProductList(query,
                new RequestCallback<SearchResult<DevUnit>>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(SearchResult<DevUnit> result) {
                        fillViewHandler = taskService.startPeriodicTask( result.getResults(), fillViewer, 50, 50 );
                    }
                });
    }

    Consumer<DevUnit> fillViewer = new Consumer<DevUnit> () {
        @Override
        public void accept( DevUnit product ) {
            AbstractProductItemView itemView = makeView(product);

            itemViewToModel.put( itemView, product );
            view.getChildContainer().add( itemView.asWidget() );
        }
    };

    @Inject
    PlateListAnimation animation;
    @Inject
    AbstractProductListView view;
    @Inject
    Provider<AbstractProductItemView> factory;
    @Inject
    PeriodicTaskService taskService;
    @Inject
    ProductControllerAsync productService;
    @Inject
    PolicyService policyService;
    @Inject
    Lang lang;

    private PeriodicTaskService.PeriodicTaskHandler fillViewHandler;
    private AppEvents.InitDetails init;
    private ProductQuery query;
    private Map< AbstractProductItemView, DevUnit> itemViewToModel = new HashMap<AbstractProductItemView, DevUnit>();

}
