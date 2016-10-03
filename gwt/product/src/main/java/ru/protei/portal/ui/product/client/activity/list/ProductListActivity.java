package ru.protei.portal.ui.product.client.activity.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Product;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;
import ru.protei.portal.ui.product.client.service.ProductServiceAsync;

import java.util.ArrayList;
import java.util.List;

public abstract class ProductListActivity implements AbstractProductListActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow (ProductEvents.Show event) {

        this.fireEvent(new AppEvents.InitPanelName(lang.products()));
        init.parent.clear();
        init.parent.add(view.asWidget());

        view.getItemsContainer().clear();

        initProducts();
    }

    private void initProducts() {

        products.clear();

        productService.getProductList(view.getName(), new RequestCallback<List<Product>>() {
            @Override
            public void onError(Throwable throwable) {
                patchView();
                fillView();
            }

            @Override
            public void onSuccess(List<Product> result) {
                products.addAll(result);
                fillView();
            }
        });
    }

    private void patchView ()
    {
        // временная заглушка
        products = new ArrayList<Product>();
        Product pr = new Product();
        pr.setPname("EACD4");
        products.add(pr);

        pr = new Product();
        pr.setPname("WelcomSMS");
        products.add(pr);
    }

    private void fillView ()
    {
        // цикл добавления items
        for (Product p : products) {

            AbstractProductItemView itemView = provider.get();
            itemView.setName(p.getPname());
            itemView.setActivity(this);
            view.getItemsContainer().add(itemView.asWidget());
        }
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }


    private List<Product> products = new ArrayList<Product>();
    private AppEvents.InitDetails init;

    @Inject
    AbstractProductListView view;
    @Inject
    Lang lang;

    @Inject
    Provider<AbstractProductItemView> provider;
    @Inject
    ProductServiceAsync productService;
}