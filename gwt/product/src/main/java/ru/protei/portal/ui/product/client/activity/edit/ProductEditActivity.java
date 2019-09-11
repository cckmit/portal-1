package ru.protei.portal.ui.product.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView.CDR_DESCRIPTION;
import static ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView.CONFIGURATION;
import static ru.protei.portal.ui.product.client.view.edit.ProductEditView.HISTORY_VERSION;

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
            product = new DevUnit();
            resetView();
            resetValidationStatus();
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
            return;
        }

        productService.isNameUnique(
                value,
                currType,
                productId,
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

        if(!isValid())
            return;

        fillDto(product);

        productService.saveProduct(product, new RequestCallback<DevUnit>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(DevUnit result) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new ProductEvents.ProductListChanged());
                resetView();
                goBack();
            }
        });
    }

    @Override
    public void onCancelClicked() {
        goBack();
    }

    @Override
    public void renderMarkdownText(String text, Consumer<String> consumer) {
        En_TextMarkup textMarkup = En_TextMarkup.MARKDOWN;
        textRenderController.render(text, textMarkup, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(text))
                .withSuccess(consumer));
    }


    @Override
    public void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( PRODUCT + "_" + key, String.valueOf( isDisplay ) );
    }

    @Override
    public void onTypeChanged(En_DevUnitType type) {
        currType = type;
        if (type.getId() != product.getTypeId()) {
            view.parents().setValue(null);
            view.children().setValue(null);
        } else {
            view.parents().setValue(product.getParents() != null ? product.getParents().stream()
                    .map(DevUnit::toProductShortView)
                    .collect(Collectors.toSet())
                    : null
            );

            view.children().setValue(product.getChildren() != null ? product.getChildren().stream()
                    .map(DevUnit::toProductShortView)
                    .collect(Collectors.toSet())
                    : null
            );
        }
    }

    private void goBack() {
        fireEvent(new Back());
    }

    private void requestProduct(Long productId) {

        productService.getProduct( productId, new RequestCallback<DevUnit>() {
            @Override
            public void onError( Throwable throwable ) {
            }

            @Override
            public void onSuccess( DevUnit devUnit ) {
                product = devUnit;
                fillView( product );
                resetValidationStatus();
            }
        } );
    }

    private void resetView () {
        view.name().setValue("");
        view.type().setValue(En_DevUnitType.COMPLEX, true);
        currType = En_DevUnitType.COMPLEX;
        view.parents().setValue(null);
        view.children().setValue(null);
        view.info().setValue("");
        view.wikiLink().setValue("");
        view.historyVersion().setValue("");
        view.configuration().setValue("");
        view.cdrDescription().setValue("");
        view.productSubscriptions().setValue(new ArrayList<>());
    }

    private void fillView(DevUnit devUnit) {

        boolean isCreate = devUnit.getId() == null;

        view.setCurrentProduct(devUnit.toProductShortView());
        view.name().setValue(devUnit.getName());
        view.type().setValue(isCreate ? En_DevUnitType.COMPLEX : devUnit.getType());
        currType = isCreate ? En_DevUnitType.COMPLEX : devUnit.getType();
        view.info().setValue(devUnit.getInfo());
        view.productSubscriptions().setValue(
                devUnit.getSubscriptions().stream()
                        .map( Subscription::fromProductSubscription )
                        .collect(Collectors.toList())
        );

        view.setMutableState(devUnit.getType());

        view.parents().setValue(devUnit.getParents() != null ? devUnit.getParents().stream()
                .map(DevUnit::toProductShortView)
                .collect(Collectors.toSet())
                : null
        );

        view.children().setValue(devUnit.getChildren() != null ? devUnit.getChildren().stream()
                .map(DevUnit::toProductShortView)
                .collect(Collectors.toSet())
                : null
        );

        view.wikiLink().setValue(devUnit.getWikiLink());

        view.setHistoryVersionPreviewAllowing( makePreviewDisplaying(HISTORY_VERSION) );
        view.setConfigurationPreviewAllowing( makePreviewDisplaying(CONFIGURATION) );
        view.setCdrDescriptionPreviewAllowed( makePreviewDisplaying(CDR_DESCRIPTION) );

        view.cdrDescription().setValue(devUnit.getCdrDescription());
        view.configuration().setValue(devUnit.getConfiguration());
        view.historyVersion().setValue(devUnit.getHistoryVersion());
    }

    private boolean makePreviewDisplaying( String key ) {
        return Boolean.parseBoolean( localStorageService.getOrDefault( PRODUCT + "_" + key, "false" ) );
    }

    private void fillDto(DevUnit product) {
        product.setName(view.name().getValue().trim());
        product.setTypeId(view.type().getValue().getId());
        product.setInfo(view.info().getValue().trim());
        product.setSubscriptions(view.productSubscriptions().getValue().stream()
                .map( Subscription::toProductSubscription )
                .collect(Collectors.toList())
        );

        Set<ProductShortView> productShortViewsParent = view.parents().getValue();

        product.setParents(productShortViewsParent != null ? productShortViewsParent.stream()
                .map(DevUnit::fromProductShortView)
                .collect(Collectors.toList()) : null
        );

        Set<ProductShortView> productShortViewsChildren = view.children().getValue();

        product.setChildren(productShortViewsChildren != null ? productShortViewsChildren.stream()
                .map(DevUnit::fromProductShortView)
                .collect(Collectors.toList()) : null
        );

        product.setWikiLink(view.wikiLink().getValue());
        product.setCdrDescription(view.cdrDescription().getValue());
        product.setConfiguration(view.configuration().getValue());
        product.setHistoryVersion(view.historyVersion().getValue());
    }

    private void resetValidationStatus(){
        view.setNameStatus(NameStatus.NONE);
    }

    private boolean isValid() {
        return view.nameValidator().isValid() &&
                view.type().getValue() != null &&
                isNameUnique;
    }

    @Inject
    AbstractProductEditView view;
    @Inject
    Lang lang;
    @Inject
    ProductControllerAsync productService;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;

    private Long productId;
    private DevUnit product;
    private boolean isNameUnique = true;
    private En_DevUnitType currType;

    private AppEvents.InitDetails init;
    private static final String PRODUCT = "product_view_is_preview_displayed";
}