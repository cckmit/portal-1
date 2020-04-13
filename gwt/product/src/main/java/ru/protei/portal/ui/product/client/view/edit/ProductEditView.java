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
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.stringselect.input.StringSelectInput;
import ru.protei.portal.ui.common.client.widget.subscription.list.SubscriptionList;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditActivity;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView;
import ru.protei.portal.ui.product.client.widget.type.ProductTypeBtnGroup;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Вид карточки создания/редактирования продукта
 */
public class ProductEditView extends Composite implements AbstractProductEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));

//        parents.setRequestByOnLoad(false);
//        children.setRequestByOnLoad(false);

        historyVersion.setRenderer((text, consumer) -> activity.renderMarkdownText(text, consumer));
        configuration.setRenderer((text, consumer) -> activity.renderMarkdownText(text, consumer));
        cdrDescription.setRenderer((text, consumer) -> activity.renderMarkdownText(text, consumer));

        historyVersion.setDisplayPreviewHandler( new MarkdownAreaWithPreview.DisplayPreviewHandler() {
            @Override
            public void onDisplayPreviewChanged( boolean isDisplay ) {
                activity.onDisplayPreviewChanged( HISTORY_VERSION, isDisplay );
            }
        } );
        configuration.setDisplayPreviewHandler( new MarkdownAreaWithPreview.DisplayPreviewHandler() {
            @Override
            public void onDisplayPreviewChanged( boolean isDisplay ) {
                activity.onDisplayPreviewChanged( CONFIGURATION, isDisplay );
            }
        } );
        cdrDescription.setDisplayPreviewHandler( new MarkdownAreaWithPreview.DisplayPreviewHandler() {
            @Override
            public void onDisplayPreviewChanged( boolean isDisplay ) {
                activity.onDisplayPreviewChanged( CDR_DESCRIPTION, isDisplay );
            }
        } );

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
    public HasValue<En_DevUnitType> type() {
        return type;
    }

    @Override
    public void setTypeImage(String src, String title) {
        typeImage.setSrc(src);
        typeImage.setTitle(title);
    }

    @Override
    public void setTypeImageVisibility(boolean isVisible) {
        if (isVisible) {
            typeImageContainer.removeClassName("hide");
        } else {
            typeImageContainer.addClassName("hide");
        }
    }

    @Override
    public HasValidable nameValidator() { return name; }

    @Override
    public HasValue<List<Subscription>> productSubscriptions() {
        return subscriptions;
    }

    @Override
    public HasValidable productSubscriptionsValidator() {
        return subscriptions;
    }

    @Override
    public HasVisibility directionVisibility() {
        return directionContainer;
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
    public void setMutableState(En_DevUnitType type) {
        parentsContainerLabel.setInnerText(lang.belongsTo());
        checkName();

        if (type.getId() == En_DevUnitType.COMPLEX.getId()) {
            nameLabel.setInnerText(lang.complexName());
            descriptionLabel.setInnerText(lang.complexDescription());
            childrenContainerLabel.setInnerText(lang.products());

            parentsContainer.addStyleName("hide");
            childrenContainer.removeStyleName("col-md-6");
            childrenContainer.addStyleName("col-md-12");

            children.setTypes(En_DevUnitType.PRODUCT);
        } else if (type.getId() == En_DevUnitType.PRODUCT.getId()) {
            nameLabel.setInnerText(lang.productName());
            descriptionLabel.setInnerText(lang.productDescription());
            childrenContainerLabel.setInnerText(lang.components());

            parentsContainer.removeStyleName("hide");
            childrenContainer.removeStyleName("col-md-12");
            childrenContainer.addStyleName("col-md-6");

            parents.setTypes(En_DevUnitType.COMPLEX);
            children.setTypes(En_DevUnitType.COMPONENT);
        } else if (type.getId() == En_DevUnitType.COMPONENT.getId()) {
            nameLabel.setInnerText(lang.componentName());
            descriptionLabel.setInnerText(lang.componentDescription());
            childrenContainerLabel.setInnerText(lang.components());

            parentsContainer.removeStyleName("hide");
            childrenContainer.removeStyleName("col-md-12");
            childrenContainer.addStyleName("col-md-6");

            parents.setTypes(En_DevUnitType.PRODUCT, En_DevUnitType.COMPONENT);
            children.setTypes(En_DevUnitType.COMPONENT);
        }
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
    public HasValue<String> wikiLink() {
        return wikiLink;
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
    public HasEnabled saveEnabled() { return saveBtn; }

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
    public HasValue<ProductDirectionInfo> direction() {
        return direction;
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
        wikiLink.ensureDebugId(DebugIds.PRODUCT.WIKI_LINK);
        
        children.ensureDebugId(DebugIds.PRODUCT.INCLUDES);
        parents.ensureDebugId(DebugIds.PRODUCT.PRODUCTS);
        aliases.ensureDebugId(DebugIds.PRODUCT.ALIASES);

        tabWidget.setTabNameDebugId(lang.productHistoryVersion(), DebugIds.PRODUCT.TAB.HISTORY_VERSION);
        historyVersion.getElement().setId(DebugIds.PRODUCT.HISTORY_VERSION);
        tabWidget.setTabNameDebugId(lang.productConfiguration(), DebugIds.PRODUCT.TAB.CONFIGURATION);
        configuration.getElement().setId(DebugIds.PRODUCT.CONFIGURATION);
        tabWidget.setTabNameDebugId(lang.productCDRDescription(), DebugIds.PRODUCT.TAB.CDR_DESCRIPTION);
        cdrDescription.getElement().setId(DebugIds.PRODUCT.CDR_DESCRIPTION);

        direction.ensureDebugId(DebugIds.PRODUCT.DIRECTION);
        directionLabel.setId(DEBUG_ID_PREFIX + DebugIds.PRODUCT.DIRECTION_LABEL);

        saveBtn.ensureDebugId(DebugIds.PRODUCT.SAVE_BUTTON);
        cancelBtn.ensureDebugId(DebugIds.PRODUCT.CANCEL_BUTTON);
    }

    Timer changeTimer = new Timer() {
        @Override
        public void run() {
            changeTimer.cancel();
            if ( activity != null ) {
                activity.onNameChanged();
            }
        }
    };


    @UiField
    LabelElement nameLabel;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    ProductTypeBtnGroup type;
    @UiField
    HTMLPanel parentsContainer;
    @UiField
    HTMLPanel childrenContainer;
    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector parents;
    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector children;
    @Inject
    @UiField(provided = true)
    ProductDirectionButtonSelector direction;
    @UiField
    HTMLPanel directionContainer;
    @UiField
    LabelElement directionLabel;
    @UiField
    Element verifiableIcon;
    @UiField
    TextArea info;
    @UiField
    Button saveBtn;
    @UiField
    Button cancelBtn;

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
    TextBox wikiLink;
    @UiField
    LabelElement parentsContainerLabel;
    @UiField
    LabelElement childrenContainerLabel;
    @Inject
    @UiField(provided = true)
    StringSelectInput aliases;
    @UiField
    HTMLPanel aliasesContainer;
    @UiField
    DivElement typeImageContainer;
    @UiField
    ImageElement typeImage;

    AbstractProductEditActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductEditView > {}
}