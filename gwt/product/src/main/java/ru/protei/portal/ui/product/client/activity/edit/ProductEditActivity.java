package ru.protei.portal.ui.product.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.service.ProductServiceAsync;

import java.util.Date;
import java.util.List;

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

        String name = view.getName().getText();
        String info = view.getInfo().getText();

        //if (name == null || name.trim().isEmpty())
        Window.alert(name);

        productService.getProductList(name.trim(), null, En_SortField.prod_name, true,
                new RequestCallback<List<DevUnit>>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errorGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(List<DevUnit> result) {
                        if (result.size() > 0)
                            fireEvent(new NotifyEvents.Show(lang.notUniqName(), NotifyEvents.NotifyType.ERROR));
                        else
                        {
                            DevUnit product = new DevUnit();
                            product.setId(productId);
                            product.setTypeId(En_DevUnitType.PRODUCT.getId());
                            product.setName(name.trim());
                            product.setInfo(info.trim());
                            product.setStateId(view.getState().getValue() ? En_DevUnitState.ACTIVE.getId() : En_DevUnitState.DEPRECATED.getId());

                            saveProduct(product);
                        }
                    }
                });

    }

    private void saveProduct(DevUnit product) {


        if (productId == null)
            product.setCreated(new Date());
        else
            product.setLastUpdate(new Date());

        Window.alert("product = " + product.getName());

        productService.saveProduct(product, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.objectNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
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