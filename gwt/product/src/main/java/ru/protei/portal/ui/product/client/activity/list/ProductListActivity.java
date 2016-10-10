package ru.protei.portal.ui.product.client.activity.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Product;
import ru.protei.portal.ui.common.client.events.AppEvents;
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

public abstract class ProductListActivity implements AbstractProductListActivity, AbstractProductItemActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.reset();

    }

    @Event
    public void onShow (ProductEvents.Show event) {

        this.fireEvent(new AppEvents.InitPanelName(lang.products()));
        init.parent.clear();
        init.parent.add(view.asWidget());

        //fireEvent(new NotifyEvents.Show("Get Product List", "Common!", NotifyEvents.NotifyType.DEFAULT));

        initProducts();
    }


    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }


    @Override
    public void onShowDepricatedClick() {
        initProducts();
    }

    @Override
    public void onSearchClicked() {

    }

    @Override
    public void onSearchFieldKeyPress() {
        if ( view.getSearchPattern().trim().length() >= 2 )
            initProducts();
    }

    private void initProducts() {

        view.getItemsContainer().clear();
        map.clear();

        productService.getProductList(view.getSearchPattern(), view.isShowDepricated().getValue(), new RequestCallback<List<Product>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent ( new NotifyEvents.Show( "Get Product List", "Error!", NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(List<Product> result) {
                //fireEvent ( new NotifyEvents.Show( "Get Product List", "Success!", NotifyEvents.NotifyType.SUCCESS ) );
                fillView(result);
            }
        });
    }

    private void fillView (List<Product> products)
    {
        for (Product product : products) {
            AbstractProductItemView itemView = makeItemView (product);

            view.getItemsContainer().add(itemView.asWidget());
            map.put(product, itemView);
        }
    }

    private AbstractProductItemView makeItemView (Product product)
    {
        AbstractProductItemView itemView = provider.get();
        itemView.setName(product.getPname());
        itemView.setDepricated(product.getDepricated());
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

    private Map<Product, AbstractProductItemView> map = new HashMap<Product, AbstractProductItemView>();
    private AppEvents.InitDetails init;

}