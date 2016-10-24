package ru.protei.portal.ui.product.client.activity.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.ent.DevUnit;
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
    public void onInit() {
        view.setActivity(this);

    }

    @Event
    public void onShow (ProductEvents.Show event) {

        this.fireEvent(new AppEvents.InitPanelName(lang.products()));
        init.parent.clear();
        init.parent.add(view.asWidget());

        requestProducts();
    }

    @Event
    public void onAuthorize (AuthEvents.Success event) {
        view.reset();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
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

        productService.getProductList(view.getSearchPattern().getText(),
                view.isShowDeprecated().getValue() ? En_DevUnitState.DEPRECATED : null,
                view.getSortField().getValue(),
                view.getSortDir().getValue(),
                new RequestCallback<List<DevUnit>>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errorGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(List<DevUnit> result) {
                        fillViewHandler = taskService.startPeriodicTask( result, fillViewer, 50, 50 );
                    }
                });
    }

    Consumer<DevUnit> fillViewer = new Consumer<DevUnit> () {

        @Override
        public void accept( DevUnit product ) {
            AbstractProductItemView itemView = makeItemView(product);

            modelToView.put( product, itemView );
            view.getItemsContainer().add( itemView.asWidget() );
        }
    };

    private AbstractProductItemView makeItemView (DevUnit product)
    {
        AbstractProductItemView itemView = provider.get();
        itemView.setName(product.getName());
        itemView.setDeprecated(product.getStateId() > 1);
        itemView.setActivity(this);

        return itemView;
    }



    @Inject
    AbstractProductListView view;
    @Inject
    Lang lang;

    @Inject
    Provider<AbstractProductItemView> provider;
    @Inject
    ProductServiceAsync productService;

    @Inject
    PeriodicTaskService taskService;
    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private Map<DevUnit, AbstractProductItemView> modelToView = new HashMap<DevUnit, AbstractProductItemView>();
    private AppEvents.InitDetails init;

}