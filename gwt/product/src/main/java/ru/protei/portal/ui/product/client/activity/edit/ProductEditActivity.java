package ru.protei.portal.ui.product.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.service.ProductServiceAsync;

/**
 * Активность списка продуктов
 */
public abstract class ProductEditActivity implements AbstractProductEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onShow (ProductEvents.Edit event) {

        this.productId = event.productId;

        init.parent.clear();
        init.parent.add(view.asWidget());

        initProduct();

        this.fireEvent(new AppEvents.InitPanelName(productId == null ? lang.newProduct() : view.getName().getText()));

    }

    private void initProduct() {

        view.reset();

        if (productId != null)
        {
            productService.getProductById(productId, new RequestCallback<DevUnit>() {
                @Override
                public void onError(Throwable throwable) {
                    fireEvent(new NotifyEvents.Show(lang.objectNotFound(), NotifyEvents.NotifyType.ERROR));
                }

                @Override
                public void onSuccess(DevUnit devUnit) {
                    fillView(devUnit);
                }
            });
        }

    }

    @Override
    public void onSaveClicked() {

        productService.saveProduct(productId, view.getName().getText(),
                view.getInfo().getText(),
                view.getState().getValue(),
                new RequestCallback<DevUnit>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.objectNotSaved(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(DevUnit devUnit) {
                        fireEvent(new NotifyEvents.Show(lang.objectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    }

                });
    }

    @Override
    public void onCancelClicked() {

        this.fireEvent(new ProductEvents.Show () );
    }

    private void fillView(DevUnit devUnit) {

        view.setName(devUnit.getName());
        view.setInfo(devUnit.getInfo());
        view.setState(En_DevUnitState.forId(devUnit.getStateId()).equals(En_DevUnitState.ACTIVE));
    }

    @Inject
    AbstractProductEditView view;
    @Inject
    Lang lang;

    @Inject
    ProductServiceAsync productService;

    private Long productId;

    private AppEvents.InitDetails init;
}