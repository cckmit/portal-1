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
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;

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

        List<Product> products = new ArrayList();
        Product pr = new Product();
        pr.setPname("Virtual Office");
        products.add(pr);

        pr = new Product();
        pr.setPname("SMS_Firewall");
        products.add(pr);

        // цикл добавления items
        for (Product p : products) {

            AbstractProductItemView itemView = provider.get();
            itemView.setActivity(this);
            itemView.setName(p.getPname());

            view.getItemsContainer().add(itemView.asWidget());
        }
    }


    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Inject
    AbstractProductListView view;
    @Inject
    Lang lang;

    private AppEvents.InitDetails init;

    @Inject
    Provider<AbstractProductItemView> provider;
}
