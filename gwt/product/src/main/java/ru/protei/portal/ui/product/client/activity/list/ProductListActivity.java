package ru.protei.portal.ui.product.client.activity.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemActivity;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;
import ru.protei.portal.ui.product.client.service.ProductServiceAsync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void onShowDeprecatedClick() {
        requestProducts();
    }


    @Override
    public void onFilterChanged() {
        requestProducts();
    }

    private void requestProducts() {

        view.getItemsContainer().clear();
        modelToView.clear();

        productService.getProductList(view.getSearchPattern().getText(),
                view.isShowDeprecated().getValue(),
                view.getSortField().getValue(),
                view.getSortDir().getValue(),
                new RequestCallback<List<DevUnit>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent ( new NotifyEvents.Show( lang.products(), lang.errorGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(List<DevUnit> result) {
                fillView(result);
            }
        });
    }

    private void fillView (List<DevUnit> products)
    {
        for (DevUnit product : products) {
            AbstractProductItemView itemView = makeItemView (product);

            view.getItemsContainer().add(itemView.asWidget());
            modelToView.put(product, itemView);
        }
    }

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

    private Map<DevUnit, AbstractProductItemView> modelToView = new HashMap<DevUnit, AbstractProductItemView>();
    private AppEvents.InitDetails init;

}