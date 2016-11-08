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
            resetValidationStatus();

            fireEvent(new AppEvents.InitPanelName(lang.productNew()));
            return;
        }

        requestProduct(productId);
    }

    @Override
    public void onNameChanged() {
        String value = view.name().getValue().trim();

        //isNameUnique не принимает пустые строки!
        if ( value.isEmpty()) {
            view.setNameStatus(NameStatus.NONE);
            view.save().setEnabled(false);
            return;
        }

        productService.isNameUnique(
                value,
                productId,
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        view.setNameStatus(NameStatus.ERROR);
//                        view.nameValidator().setValid(true);
                    }

                    @Override
                    public void onSuccess(Boolean isUnique) {
                        view.setNameStatus(isUnique ? NameStatus.SUCCESS : NameStatus.ERROR);
                        view.save().setEnabled(isUnique);
//                        view.nameValidator().setValid(isUnique);
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
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Boolean result) {
                goBack();
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
            }
        });
    }

    @Override
    public void onCancelClicked() {
        goBack();
    }

//    private boolean validateFieldsAndGetResult(){
//        boolean isCorrect = true;
//        if(!view.nameValidator().isValid()){
//            view.nameValidator().setValid(isCorrect = false);
//        }
//
//        return isCorrect;
//    }

    private void goBack() {
        fireEvent(new Back());
    }

    private void requestProduct(Long productId) {

        productService.getProductById(productId, new RequestCallback<DevUnit>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(DevUnit devUnit) {
                fireEvent(new AppEvents.InitPanelName(product.getName()));
                fillView(product = devUnit);
                resetValidationStatus();
            }
        });
    }

    private void resetView () {
        view.name().setValue("");
        view.info().setValue("");
        view.state().setVisible(false);
        view.save().setEnabled(false);
    }

    private void fillView(DevUnit devUnit) {
        view.name().setValue(devUnit.getName());
        view.info().setValue(devUnit.getInfo());
        view.state().setVisible(true);
        view.save().setEnabled(true);

        view.setStateBtnText(devUnit.isActiveUnit() ? lang.productToArchive() : lang.productFromArchive());
    }

    private void resetValidationStatus(){
        view.setNameStatus(NameStatus.NONE);
        view.nameValidator().setValid(true);
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