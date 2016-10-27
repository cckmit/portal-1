package ru.protei.portal.ui.product.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.NameStatus;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.service.ProductServiceAsync;

/**
 * Активность карточки создания и редактирования продуктов
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

        if( productId == null ) {
            resetView();

            fireEvent(new AppEvents.InitPanelName(lang.newProduct()));
            return;
        }

        requestProduct(productId);
    }

    @Override
    public void onNameChanged() {
        view.setNameStatus(NameStatus.UNDEFINED);

        if (view.name().getValue() == null || view.name().getValue().trim().isEmpty()) {
            view.setNameStatus(NameStatus.ERROR);
            return;
        }

        productService.isNameUnique(view.name().getValue().trim(), productId,
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        view.setNameStatus(NameStatus.ERROR);
                        view.save().setEnabled(false);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        view.setNameStatus(result ? NameStatus.SUCCESS : NameStatus.ERROR);
                        view.save().setEnabled(result);
                    }
                });
    }

    @Override
    public void onStateChanged() {
        product.setStateId(product.isActiveUnit() ? En_DevUnitState.DEPRECATED.getId() : En_DevUnitState.ACTIVE.getId());
    }

    @Override
    public void onSaveClicked() {

        if (productId == null) {
            product = new DevUnit();
        }

        product.setName(view.name().getValue().trim());
        product.setInfo(view.info().getValue().trim());

        productService.saveProduct(product, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errorSave(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                goBack();
                fireEvent(new NotifyEvents.Show(lang.objectSaved(), NotifyEvents.NotifyType.SUCCESS));
            }
        });
    }

    @Override
    public void onCancelClicked() {
        goBack();
    }

    private void goBack() {
        fireEvent(new Back());
    }

    private void requestProduct(Long productId) {

        productService.getProductById(productId, new RequestCallback<DevUnit>() {
            @Override
            public void onError(Throwable throwable) {
                goBack();
                fireEvent(new NotifyEvents.Show(lang.objectNotFound(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(DevUnit devUnit) {
                product = devUnit;
                fillView(devUnit);
                fireEvent(new AppEvents.InitPanelName(product.getName()));
            }
        });
    }

    private void resetView () {
        view.name().setValue("");
        view.info().setValue("");
        view.save().setEnabled(false);
        view.setNameStatus(NameStatus.UNDEFINED);
    }

    private void fillView(DevUnit devUnit) {
        view.name().setValue(devUnit.getName());
        view.setNameStatus(NameStatus.SUCCESS);
        view.info().setValue(devUnit.getInfo());
        view.state().setVisible(true);
        view.save().setEnabled(true);

        view.setStateBtnText(devUnit.isActiveUnit() ? lang.buttonArchive() : lang.buttonFromArchive());
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