package ru.protei.portal.ui.product.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView.*;
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
        if (!hasPrivileges(event.productId)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        init.parent.clear();
        init.parent.add(view.asWidget());

        productId = event.productId;

        if(productId == null) {
            product = new DevUnit();
            fillView(product);
            resetValidationStatus();
            return;
        }

        requestProduct(productId);
    }

    @Override
    public void checkName() {
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
                fireEvent(isNew(product) ? new ProductEvents.Show(true) : new Back());
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
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
        if (type.equals(currType)) {
            return;
        }

        currType = type;

        view.parents().setValue(null);
        view.children().setValue(null);
        view.aliases().setValue(null);
        view.aliasesVisibility().setVisible(type.equals(En_DevUnitType.PRODUCT));
        view.directionVisibility().setVisible(!En_DevUnitType.COMPONENT.equals(type));

        view.setMutableState(type);
        checkName();
    }

    private boolean isNew(DevUnit product) {
        return product.getId() == null;
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

    private void fillView(DevUnit devUnit) {

        boolean isNew = isNew(devUnit);

        view.setCurrentProduct(isNew ? null : devUnit.toProductShortView());
        view.name().setValue(devUnit.getName());
        view.info().setValue(devUnit.getInfo());

        currType = isNew ? En_DevUnitType.COMPLEX : devUnit.getType();
        view.type().setValue(currType);
        view.typeVisibility().setVisible(isNew);
        view.setTypeImage(isNew || devUnit.getType() == null  ? null : devUnit.getType().getImgSrc(), typeLang.getName(devUnit.getType()));
        view.setTypeImageVisibility(!isNew);
        view.setMutableState(currType);
        checkName();

        view.productSubscriptions().setValue(devUnit.getSubscriptions() == null ? null : devUnit.getSubscriptions().stream()
                .map(Subscription::fromProductSubscription)
                .collect(Collectors.toList())
        );

        view.parents().setValue(devUnit.getParents() == null ? null : devUnit.getParents().stream()
                .map(DevUnit::toProductShortView)
                .collect(Collectors.toSet())
        );

        view.children().setValue(devUnit.getChildren() == null ? null : devUnit.getChildren().stream()
                .map(DevUnit::toProductShortView)
                .collect(Collectors.toSet())
        );

        view.direction().setValue(devUnit.getProductDirection() == null ? null : devUnit.getProductDirection().toProductDirectionInfo());

        view.wikiLink().setValue(devUnit.getWikiLink());

        view.setHistoryVersionPreviewAllowing( makePreviewDisplaying(HISTORY_VERSION) );
        view.setConfigurationPreviewAllowing( makePreviewDisplaying(CONFIGURATION) );
        view.setCdrDescriptionPreviewAllowed( makePreviewDisplaying(CDR_DESCRIPTION) );
        view.setInfoPreviewAllowed(makePreviewDisplaying(INFO));

        view.cdrDescription().setValue(devUnit.getCdrDescription());
        view.configuration().setValue(devUnit.getConfiguration());
        view.historyVersion().setValue(devUnit.getHistoryVersion());

        view.aliases().setValue(product.getAliases());
        view.aliasesVisibility().setVisible(currType.equals(En_DevUnitType.PRODUCT));
    }

    private boolean makePreviewDisplaying( String key ) {
        return localStorageService.getBooleanOrDefault(PRODUCT + "_" + key, false);
    }

    private void fillDto(DevUnit product) {

        product.setName(view.name().getValue().trim());
        product.setInfo(view.info().getValue().trim());

        boolean isCreate = product.getId() == null;
        if (isCreate) {
            product.setType(view.type().getValue());
        }

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

        product.setProductDirection(DevUnit.fromProductDirectionInfo(view.direction().getValue()));

        product.setWikiLink(view.wikiLink().getValue());
        product.setCdrDescription(view.cdrDescription().getValue());
        product.setConfiguration(view.configuration().getValue());
        product.setHistoryVersion(view.historyVersion().getValue());

        product.setAliases(view.aliases().getValue());
    }

    private void resetValidationStatus(){
        view.setNameStatus(NameStatus.NONE);
    }

    private boolean isValid() {
        return view.nameValidator().isValid() &&
                view.type().getValue() != null &&
                isNameUnique;
    }

    private boolean hasPrivileges(Long productId) {
        if (productId == null && policyService.hasPrivilegeFor(En_Privilege.PRODUCT_CREATE)) {
            return true;
        }

        if (productId != null && policyService.hasPrivilegeFor(En_Privilege.PRODUCT_EDIT)) {
            return true;
        }

        return false;
    }

    @Inject
    AbstractProductEditView view;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;
    @Inject
    ProductControllerAsync productService;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    En_DevUnitTypeLang typeLang;

    private Long productId;
    private DevUnit product;
    private boolean isNameUnique = true;
    private En_DevUnitType currType;

    private AppEvents.InitDetails init;
    private static final String PRODUCT = "product_view_is_preview_displayed";
}
