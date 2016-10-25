package ru.protei.portal.ui.product.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.service.ProductServiceAsync;

import java.util.Date;

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

        init.parent.clear();
        init.parent.add(view.asWidget());

        productId = event.productId;

        initProduct(productId);

        this.fireEvent(new AppEvents.InitPanelName(productId == null ? lang.newProduct() : lang.changeProduct()));

    }

    private void initProduct(Long productId) {

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
                    product = devUnit;
                    fillView(devUnit);
                }
            });
        }
    }

    @Override
    public void checkName() {

        productService.isNameExist(view.getName().getText().trim(), productId,
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.error(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        view.setNameChecked(result.booleanValue());
                    }
                });
    }

    @Override
    public void onSaveClicked() {

        if (product == null)
        {
            product = new DevUnit();
            product.setId(productId);
            product.setTypeId(En_DevUnitType.PRODUCT.getId());
            product.setCreated(new Date());
            //product.setLastUpdate(null);
        }
//        else
//            product.setLastUpdate(new Date());

        product.setName(view.getName().getText().trim());
        product.setInfo(view.getInfo().getText().trim());
        product.setStateId(view.getState().getValue() ? En_DevUnitState.ACTIVE.getId() : En_DevUnitState.DEPRECATED.getId());

        productService.saveProduct(product, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errorSave(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                fireEvent(new NotifyEvents.Show(lang.objectSaved(), NotifyEvents.NotifyType.SUCCESS));
                goBack();
            }

        });

    }


    @Override
    public void onCancelClicked() {
        goBack();
    }

    public void goBack()
    {
        productId = null;
        product = null;
        fireEvent(new Back());
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
    private DevUnit product;

    private AppEvents.InitDetails init;
}