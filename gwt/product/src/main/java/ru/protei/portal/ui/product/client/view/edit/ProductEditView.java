package ru.protei.portal.ui.product.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.markdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionMultiSelector;
import ru.protei.portal.ui.common.client.widget.stringselect.input.StringSelectInput;
import ru.protei.portal.ui.common.client.widget.subscription.list.SubscriptionList;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditActivity;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView;
import ru.protei.portal.ui.product.client.widget.type.ProductTypeBtnGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.getFirst;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

/**
 * Вид карточки создания/редактирования продукта
 */
public class ProductEditView extends Composite implements AbstractProductEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));

        historyVersion.setRenderer((text, consumer) -> activity.renderMarkdownText(text, consumer));
        configuration.setRenderer((text, consumer) -> activity.renderMarkdownText(text, consumer));
        cdrDescription.setRenderer((text, consumer) -> activity.renderMarkdownText(text, consumer));
        info.setRenderer((text, consumer) -> activity.renderMarkdownText(text, consumer));

        historyVersion.setDisplayPreviewHandler(isDisplay -> activity.onDisplayPreviewChanged( HISTORY_VERSION, isDisplay ));
        configuration.setDisplayPreviewHandler(isDisplay -> activity.onDisplayPreviewChanged( CONFIGURATION, isDisplay ));
        cdrDescription.setDisplayPreviewHandler(isDisplay -> activity.onDisplayPreviewChanged( CDR_DESCRIPTION, isDisplay ));
        info.setDisplayPreviewHandler(isDisplay -> activity.onDisplayPreviewChanged(INFO, isDisplay));
        commonManagerModel.setIsPeople(false);
        commonManager.setAsyncPersonModel( commonManagerModel );

        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractProductEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setCurrentProduct(ProductShortView product) {
        parents.setFilter( p -> !Objects.equals(p, product) );
        children.setFilter( p -> !Objects.equals(p, product) );
    }

    @Override
    public HasValue<String> name() { return name; }

    @Override
    public void setTypeAndDirections(En_DevUnitType type, Set<ProductDirectionInfo> directions) {
        this.type.setValue(type);

        productDirection.setValue(null);
        productMultiDirections.setValue(null);

        if (this.type.getValue() == En_DevUnitType.COMPLEX) {
            productMultiDirections.setValue(directions);
        } else {
            productDirection.setValue(directions == null ? null : getFirst(directions));
        }
    }

    @Override
    public Pair<En_DevUnitType, Set<ProductDirectionInfo>> getTypeAndDirections() {
        Set<ProductDirectionInfo> directions;
        if (this.type.getValue() == En_DevUnitType.COMPLEX) {
            directions = productMultiDirections.getValue();
        } else {
            directions = new HashSet<>();
            if (productDirection.getValue() != null) {
                directions.add(productDirection.getValue());
            }
        }
        return new Pair<>(type.getValue(), directions);
    }

    @Override
    public void setTypeImage(String src, String title) {
        typeImage.setSrc(src);
        typeImage.setTitle(title);
    }

    @Override
    public void setTypeImageVisibility(boolean isVisible) {
        if (isVisible) {
            typeImageContainer.removeClassName(HIDE);
        } else {
            typeImageContainer.addClassName(HIDE);
        }
    }

    @Override
    public HasValidable nameValidator() { return name; }

    @Override
    public HasValue<List<Subscription>> productSubscriptions() {
        return subscriptions;
    }

    @Override
    public HasVisibility directionContainerVisibility() {
        return directionContainer;
    }

    @Override
    public void directionSelectorVisibility(boolean isMulti) {
        productDirection.setVisible(!isMulti);
        productMultiDirections.setVisible(isMulti);
    }

    @Override
    public void setHistoryVersionPreviewAllowing( boolean isPreviewAllowed ) {
        historyVersion.setDisplayPreview( isPreviewAllowed );
    }

    @Override
    public void setConfigurationPreviewAllowing( boolean isPreviewAllowed ) {
        configuration.setDisplayPreview( isPreviewAllowed );
    }

    @Override
    public void setCdrDescriptionPreviewAllowed( boolean isPreviewAllowed ) {
       cdrDescription.setDisplayPreview( isPreviewAllowed );
    }

    @Override
    public void setInfoPreviewAllowed(boolean isPreviewAllowed) {
        info.setDisplayPreview(isPreviewAllowed);
    }

    @Override
    public HasValue<String> info() { return info; }

    @Override
    public HasValue<Set<ProductShortView>> parents() {
        return parents;
    }

    @Override
    public HasValue<Set<ProductShortView>> children() {
        return children;
    }

    @Override
    public HasValue<String> internalDocLink() {
        return internalDocLink;
    }

    @Override
    public HasValue<String> historyVersion() {
        return historyVersion;
    }

    @Override
    public HasValue<String> configuration() {
        return configuration;
    }

    @Override
    public HasValue<String> cdrDescription() {
        return cdrDescription;
    }

    @Override
    public void setNameStatus (NameStatus status)
    {
        verifiableIcon.setClassName(status.getStyle());
    }

    @Override
    public HasValue<List<String>> aliases() {
        return aliases;
    }

    @Override
    public HasVisibility aliasesVisibility() {
        return aliasesContainer;
    }

    @Override
    public HasVisibility typeVisibility() {
        return type;
    }

    @Override
    public HasValue<PersonShortView> commonManager() {
        return commonManager;
    }

    @Override
    public void setCommonManagerCompanyId(Long id) {
        commonManagerModel.updateCompanies( commonManager, setOf(id) );
    }

    @Override
    public HasVisibility commonManagerContainerVisibility() {
        return commonManagerContainer;
    }

    @Override
    public HasVisibility parentsContainerVisibility() {
        return parents;
    }

    @Override
    public void makeChildrenContainerShortView() {
        children.getElement().replaceClassName(UiConstants.Styles.FULL_VIEW, UiConstants.Styles.SHORT_VIEW);
    }

    @Override
    public void makeChildrenContainerFullView() {
        children.getElement().replaceClassName(UiConstants.Styles.SHORT_VIEW, UiConstants.Styles.FULL_VIEW);
    }

    @Override
    public void makeDirectionContainerShortView() {
        directionContainer.getElement().replaceClassName(UiConstants.Styles.FULL_VIEW, UiConstants.Styles.SHORT_VIEW);
    }

    @Override
    public void makeDirectionContainerFullView() {
        directionContainer.getElement().replaceClassName(UiConstants.Styles.SHORT_VIEW, UiConstants.Styles.FULL_VIEW);
    }

    @Override
    public void setParentTypes(En_DevUnitType... types) {
        parents.setTypes(types);
    }

    @Override
    public void setChildrenTypes(En_DevUnitType... types) {
        children.setTypes(types);
    }

    @Override
    public void setNameLabel(String label) {
        nameLabel.setInnerText(label);
    }

    @Override
    public void setDescriptionLabel(String label) {
        descriptionLabel.setInnerText(label);
    }

    @Override
    public void setChildrenContainerLabel(String label) {
        children.setHeader(label);
    }

    @UiHandler("saveBtn")
    public void onSaveClicked(ClickEvent event)
    {
        if ( activity != null )
            activity.onSaveClicked();
    }

    @UiHandler("cancelBtn")
    public void onCancelClicked(ClickEvent event)
    {
        if ( activity != null )
            activity.onCancelClicked();
    }

    @UiHandler( "name" )
    public void onNameKeyUp (KeyUpEvent event) {
        checkName();
    }

    @UiHandler( "name" )
    public void onBlur (BlurEvent event) {
        checkName();
    }

    @UiHandler( "type" )
    public void onTypeChanged(ValueChangeEvent<En_DevUnitType> event) {
        if (activity != null) {
            activity.onTypeChanged(event.getValue());
        }
    }

    private void checkName () {
        setNameStatus(NameStatus.UNDEFINED);

        changeTimer.cancel();
        changeTimer.schedule(300);
    }

    private void ensureDebugIds() {
        name.ensureDebugId(DebugIds.PRODUCT.NAME);
        info.ensureDebugId(DebugIds.PRODUCT.DESCRIPTION);
        internalDocLink.ensureDebugId(DebugIds.PRODUCT.INTERNAL_DOC_LINK);
        
        children.ensureDebugId(DebugIds.PRODUCT.INCLUDES);
        parents.ensureDebugId(DebugIds.PRODUCT.PRODUCTS);
        aliases.ensureDebugId(DebugIds.PRODUCT.ALIASES);

        commonManager.ensureDebugId(DebugIds.PRODUCT.COMMON_MANAGER);

        tabWidget.setTabNameDebugId(lang.productHistoryVersion(), DebugIds.PRODUCT.TAB.HISTORY_VERSION);
        historyVersion.getElement().setId(DebugIds.PRODUCT.HISTORY_VERSION);
        tabWidget.setTabNameDebugId(lang.productConfiguration(), DebugIds.PRODUCT.TAB.CONFIGURATION);
        configuration.getElement().setId(DebugIds.PRODUCT.CONFIGURATION);
        tabWidget.setTabNameDebugId(lang.productCDRDescription(), DebugIds.PRODUCT.TAB.CDR_DESCRIPTION);
        cdrDescription.getElement().setId(DebugIds.PRODUCT.CDR_DESCRIPTION);

        productDirection.ensureDebugId(DebugIds.PRODUCT.DIRECTION);
        productMultiDirections.ensureDebugId(DebugIds.PRODUCT.DIRECTIONS);

        saveBtn.ensureDebugId(DebugIds.PRODUCT.SAVE_BUTTON);
        cancelBtn.ensureDebugId(DebugIds.PRODUCT.CANCEL_BUTTON);
    }

    @UiField
    LabelElement nameLabel;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    ProductTypeBtnGroup type;
    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector parents;
    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector children;
    @Inject
    @UiField(provided = true)
    ProductDirectionButtonSelector productDirection;
    @Inject
    @UiField( provided = true )
    ProductDirectionMultiSelector productMultiDirections;
    @UiField
    HTMLPanel directionContainer;
    @UiField
    Element verifiableIcon;
    @UiField
    MarkdownAreaWithPreview info;
    @UiField
    Button saveBtn;
    @UiField
    Button cancelBtn;
    @Inject
    @UiField(provided = true)
    PersonButtonSelector commonManager;
    @UiField
    HTMLPanel commonManagerContainer;

    @Inject
    @UiField
    Lang lang;
    @Inject
    @UiField( provided = true )
    SubscriptionList subscriptions;
    @UiField
    TabWidget tabWidget;
    @UiField
    MarkdownAreaWithPreview historyVersion;
    @UiField
    MarkdownAreaWithPreview configuration;
    @UiField
    MarkdownAreaWithPreview cdrDescription;
    @UiField
    TextBox internalDocLink;
    @Inject
    @UiField(provided = true)
    StringSelectInput aliases;
    @UiField
    HTMLPanel aliasesContainer;
    @UiField
    DivElement typeImageContainer;
    @UiField
    ImageElement typeImage;

    @Inject
    PersonModel commonManagerModel;

    private Timer changeTimer = new Timer() {
        @Override
        public void run() {
            activity.onNameChanged();
        }
    };

    private AbstractProductEditActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductEditView > {}
}
