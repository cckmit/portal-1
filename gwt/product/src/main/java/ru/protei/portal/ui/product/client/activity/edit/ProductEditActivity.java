package ru.protei.portal.ui.product.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.toSet;
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
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        init.parent.clear();
        Window.scrollTo(0, 0);
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
    public void onNameChanged() {
        String name = view.name().getValue().trim();
        checkName(name);
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
                fireEvent(new ProductEvents.Show(!isNew(product)));
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new ProductEvents.Show(!isNew(product)));
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
        view.directionContainerVisibility().setVisible(!En_DevUnitType.COMPONENT.equals(type));
        view.directionSelectorVisibility(En_DevUnitType.COMPLEX == type);

        setMutableState(view, type);
        String trim = view.name().getValue().trim();
        checkName(trim);
    }

    private void checkName(String name) {
        //isNameUnique не принимает пустые строки!
        if ( name.isEmpty()) {
            view.setNameStatus(NameStatus.NONE);
            return;
        }

        productService.isNameUnique(
                name,
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
        Set<ProductDirectionInfo> directions = devUnit.getProductDirections() == null ? null
                : new HashSet<>(toSet(devUnit.getProductDirections(), DevUnit::toProductDirectionInfo));
        view.setTypeAndDirections(currType, directions);
        view.directionSelectorVisibility(currType == En_DevUnitType.COMPLEX);
        view.typeVisibility().setVisible(isNew);
        view.setTypeImage(isNew || devUnit.getType() == null  ? null : devUnit.getType().getImgSrc(), typeLang.getName(devUnit.getType()));
        view.setTypeImageVisibility(!isNew);
        setMutableState(view, currType);
        isNameUnique = true;

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

        view.internalDocLink().setValue(devUnit.getInternalDocLink());
        view.externalDocLink().setValue(devUnit.getExternalDocLink());

        view.setHistoryVersionPreviewAllowing( isPreviewDisplayed(HISTORY_VERSION) );
        view.setConfigurationPreviewAllowing( isPreviewDisplayed(CONFIGURATION) );
        view.setCdrDescriptionPreviewAllowed( isPreviewDisplayed(CDR_DESCRIPTION) );
        view.setInfoPreviewAllowed(isPreviewDisplayed(INFO));

        view.cdrDescription().setValue(devUnit.getCdrDescription());
        view.configuration().setValue(devUnit.getConfiguration());
        view.historyVersion().setValue(devUnit.getHistoryVersion());

        view.aliases().setValue(product.getAliases());
        view.aliasesVisibility().setVisible(currType.equals(En_DevUnitType.PRODUCT));

        view.setCommonManagerCompanyId(CrmConstants.Company.HOME_COMPANY_ID);
        view.commonManager().setValue(devUnit.getCommonManagerId() == null ? null : new PersonShortView(devUnit.getCommonManagerName(), devUnit.getCommonManagerId()));
    }

    private boolean isPreviewDisplayed(String key) {
        return localStorageService.getBooleanOrDefault(PRODUCT + "_" + key, false);
    }

    private void fillDto(DevUnit product) {

        product.setName(view.name().getValue().trim());
        product.setInfo(view.info().getValue().trim());

        boolean isCreate = product.getId() == null;
        final Pair<En_DevUnitType, Set<ProductDirectionInfo>> typeAndDirections = view.getTypeAndDirections();
        if (isCreate) {
            product.setType(typeAndDirections.getA());
        }
        product.setProductDirections(toSet(typeAndDirections.getB(), DevUnit::fromProductDirectionInfo));

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

        product.setInternalDocLink(view.internalDocLink().getValue());
        product.setExternalDocLink(view.externalDocLink().getValue());
        product.setCdrDescription(view.cdrDescription().getValue());
        product.setConfiguration(view.configuration().getValue());
        product.setHistoryVersion(view.historyVersion().getValue());

        product.setAliases(view.aliases().getValue());

        product.setCommonManagerId(view.commonManager().getValue() == null ? null : view.commonManager().getValue().getId());
    }

    private void resetValidationStatus(){
        view.setNameStatus(NameStatus.NONE);
    }

    private boolean isValid() {
        return view.nameValidator().isValid() &&
                view.getTypeAndDirections().getA() != null &&
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

    private void setMutableState(AbstractProductEditView view, En_DevUnitType type) {
        view.commonManagerContainerVisibility().setVisible(En_DevUnitType.PRODUCT.equals(type));
        view.parentsContainerVisibility().setVisible(!En_DevUnitType.COMPLEX.equals(type));

        if (En_DevUnitType.COMPLEX.equals(type)) {
            view.setNameLabel(lang.complexName());
            view.setDescriptionLabel(lang.complexDescription());
            view.setChildrenContainerLabel(lang.products());

            view.makeChildrenContainerFullView();

            view.setChildrenTypes(En_DevUnitType.PRODUCT);

            view.makeDirectionContainerFullView();
        } else if (En_DevUnitType.PRODUCT.equals(type)) {
            view.setNameLabel(lang.productName());
            view.setDescriptionLabel(lang.productDescription());
            view.setChildrenContainerLabel(lang.components());

            view.makeChildrenContainerShortView();

            view.setParentTypes(En_DevUnitType.COMPLEX);
            view.setChildrenTypes(En_DevUnitType.COMPONENT);

            view.makeDirectionContainerShortView();
        } else if (En_DevUnitType.COMPONENT.equals(type)) {
            view.setNameLabel(lang.componentName());
            view.setDescriptionLabel(lang.componentDescription());
            view.setChildrenContainerLabel(lang.components());

            view.makeChildrenContainerShortView();

            view.setParentTypes(En_DevUnitType.PRODUCT, En_DevUnitType.COMPONENT);
            view.setChildrenTypes(En_DevUnitType.COMPONENT);

            view.makeDirectionContainerFullView();
        }
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
