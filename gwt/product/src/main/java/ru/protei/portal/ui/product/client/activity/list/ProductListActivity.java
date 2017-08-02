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
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.PlateListAnimation;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterActivity;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterView;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemActivity;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Активность списка продуктов
 */
public abstract class ProductListActivity implements AbstractProductListActivity, AbstractProductItemActivity, AbstractProductFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity(this);
        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( ProductEvents.Show event ) {
        this.fireEvent(new AppEvents.InitPanelName(lang.products()));
        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.PRODUCT_CREATE ) ?
            new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.PRODUCT ) :
            new ActionBarEvents.Clear()
        );

        query = makeQuery();
        requestProducts();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.PRODUCT.equals( event.identity ) ) {
            return;
        }

        fireEvent(new ProductEvents.Edit(null));
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Override
    public void onCreateClicked( ) { fireEvent(new ProductEvents.Edit()); }

    @Override
    public void onEditClicked( AbstractProductItemView itemView ) {
        fireEvent( new ProductEvents.Edit( itemViewToModel.get( itemView ).getId()  ) );
    }

    @Override
    public void onPreviewClicked( AbstractProductItemView itemView ) {
        DevUnit value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent( new ProductEvents.ShowPreview( itemView.getPreviewContainer(), value ) );
        animation.showPreview(itemView, (IsWidget) itemView.getPreviewContainer());
    }

    @Override
    public void onFavoriteClicked( AbstractProductItemView itemView ) {
        Window.alert( "On favorite clicked" );
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        requestProducts();
    }

    private void requestProducts() {

        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.getChildContainer().clear();
        view.setListCreateBtnVisible(policyService.hasPrivilegeFor( En_Privilege.PRODUCT_CREATE ));
        itemViewToModel.clear();

        productService.getProductList(query,
                new RequestCallback<List<DevUnit>>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(List<DevUnit> result) {
                        fillViewHandler = taskService.startPeriodicTask( result, fillViewer, 50, 50 );
                    }
                });
    }

    private ProductQuery makeQuery() {
        query = new ProductQuery();
        query.setSearchString(filterView.searchPattern().getValue());
        query.setState(filterView.showDeprecated().getValue() ? null : En_DevUnitState.ACTIVE);
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);

        return query;
    };

    private AbstractProductItemView makeView ( DevUnit product )
    {
        AbstractProductItemView itemView = factory.get();
        itemView.setName(product.getName());
        itemView.setDeprecated(product.getStateId() > 1);
        itemView.setActivity(this);
        itemView.setEditEnabled( policyService.hasPrivilegeFor( En_Privilege.PRODUCT_EDIT ) );

        return itemView;
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
    AbstractProductListView view;
    @Inject
    AbstractProductFilterView filterView;
    @Inject
    Lang lang;
    @Inject
    Provider<AbstractProductItemView> factory;
    @Inject
    ProductServiceAsync productService;
    @Inject
    PlateListAnimation animation;
    @Inject
    PeriodicTaskService taskService;
    @Inject
    PolicyService policyService;
    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private Map<AbstractProductItemView, DevUnit > itemViewToModel = new HashMap<AbstractProductItemView, DevUnit>();
    private AppEvents.InitDetails init;
    private ProductQuery query;

    private static String CREATE_ACTION;
}