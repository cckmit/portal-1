package ru.protei.portal.ui.product.client.activity.quickcreate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

/**
 * Активность создания продукта с минимальным набором параметров
 */
public abstract class ProductCreateActivity implements AbstractProductCreateActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ProductEvents.QuickCreate event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        initialView(new DevUnit());
    }

    @Override
    public void onNameChanged() {
        String value = view.name().getValue().trim();

        //isNameUnique не принимает пустые строки!
        if (value.isEmpty()) {
            view.setNameStatus(NameStatus.NONE);
            return;
        }

        productService.isNameUnique(
                value,
                null,
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        view.setNameStatus(NameStatus.ERROR);
                    }

                    @Override
                    public void onSuccess(Boolean isUnique) {
                        view.setNameStatus(isUnique ? NameStatus.SUCCESS : NameStatus.ERROR);
                        isNameUnique = isUnique;
                    }
                });
    }

    @Override
    public void onSaveClicked() {

        if(!validate()) {
            return;
        }

        fillProduct();

        productService.saveProduct(product, new RequestCallback<DevUnit>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(DevUnit product) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new ProductEvents.ProductListChanged());
                fireEvent(new ProductEvents.Set(product));
                initialView(new DevUnit());
            }
        });
    }

    @Override
    public void onResetClicked() {
        initialView(new DevUnit());
    }

    private void initialView(DevUnit product) {
        this.product = product;
        view.name().setValue(product.getName());
        view.info().setValue(product.getInfo());
        view.setNameStatus(NameStatus.NONE);
    }

    private void fillProduct() {
        product.setTypeId(En_DevUnitType.PRODUCT.getId());
        product.setName(view.name().getValue().trim());
        product.setInfo(view.info().getValue().trim());
    }

    private boolean validate() {
        return view.nameValidator().isValid() &&
                isNameUnique;
    }

    @Inject
    AbstractProductCreateView view;
    @Inject
    ProductControllerAsync productService;
    @Inject
    Lang lang;

    private DevUnit product;
    private boolean isNameUnique = true;
}
