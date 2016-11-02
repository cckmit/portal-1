package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.animation.PlateListAnimation;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PeriodicTaskService;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemActivity;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;
import ru.protei.portal.ui.product.client.service.ProductServiceAsync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Активность списка продуктов
 */
public abstract class ProductListActivity implements AbstractProductListActivity, AbstractProductItemActivity, Activity {

    @PostConstruct
    public void onInit() { view.setActivity(this); }

    @Event
    public void onAuthorize (AuthEvents.Success event) {
        resetFilter();
    }

    @Event
    public void onShow (ProductEvents.Show event) {

        this.fireEvent(new AppEvents.InitPanelName(lang.products()));
        init.parent.clear();
        init.parent.add(view.asWidget());

        requestProducts();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Override
    public void onCreateClicked( ) { fireEvent(new ProductEvents.Edit()); }

    @Override
    public void onUpdateClicked( AbstractProductItemView itemView ) {

        fireEvent( new ProductEvents.Edit( modelToView.get( itemView ).getId() ) );
    }

    @Override
    public void onMenuClicked( AbstractProductItemView itemView ) {
        DevUnit value = modelToView.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent( new ProductEvents.ShowPreview( itemView.getPreviewContainer(), value ) );
        animation.showPreview(itemView, (IsWidget) itemView.getPreviewContainer());
    }

    @Override
    public void onFilterChanged() {
        requestProducts();
    }


    private void requestProducts() {

        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.getItemsContainer().clear();
        modelToView.clear();

        productService.getProductList(view.searchPattern().getValue(),
                view.showDeprecated().getValue() ? null : En_DevUnitState.ACTIVE,
                view.sortField().getValue(),
                view.sortDir().getValue(),
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

    private AbstractProductItemView makeItemView (DevUnit product)
    {
        AbstractProductItemView itemView = provider.get();
        itemView.setName(product.getName());
        itemView.setDeprecated(product.getStateId() > 1);
        itemView.setActivity(this);

        return itemView;
    }

    private void resetFilter() {
        view.searchPattern().setValue("");
        view.showDeprecated().setValue(false);
        view.sortField().setValue(En_SortField.prod_name);
        view.sortDir().setValue(true);
    }

    Consumer<DevUnit> fillViewer = new Consumer<DevUnit> () {

        @Override
        public void accept( DevUnit product ) {
            AbstractProductItemView itemView = makeItemView(product);

            modelToView.put( itemView, product );
            view.getItemsContainer().add( itemView.asWidget() );
        }
    };

    @Inject
    AbstractProductListView view;
    @Inject
    Lang lang;

    @Inject
    Provider<AbstractProductItemView> provider;
    @Inject
    ProductServiceAsync productService;
    @Inject
    PlateListAnimation animation;
    @Inject
    PeriodicTaskService taskService;
    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private Map<AbstractProductItemView, DevUnit > modelToView = new HashMap<AbstractProductItemView, DevUnit>();
    private AppEvents.InitDetails init;
}