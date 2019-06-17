package ru.protei.portal.ui.product.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.selector.product.component.ComponentMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.subscription.list.SubscriptionList;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditActivity;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView;
import ru.protei.portal.ui.product.client.widget.type.ProductTypeBtnGroup;

import java.util.List;
import java.util.Set;

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
    }

    @Override
    public void setActivity(AbstractProductEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setCurrentProduct(ProductShortView product) {
        devUnits.exclude(product);
        components.exclude(product);
    }

    @Override
    public HasValue<String> name() { return name; }

    @Override
    public HasValue<En_DevUnitType> type() {
        return type;
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
    public void setIsProduct(boolean isProduct) {
        if (isProduct) {
            nameLabel.setInnerText(lang.productName());
            devUnitContainer.addStyleName("hide");
        } else {
            nameLabel.setInnerText(lang.componentName());
            devUnitContainer.removeStyleName("hide");
        }
    }

    @Override
    public HasValue<String> info() { return info; }

    @Override
    public HasValue<Set<ProductShortView>> parents() {
        return devUnits;
    }

    @Override
    public HasValue<Set<ProductShortView>> components() {
        return components;
    }

    @Override
    public HasVisibility state() { return stateBtn; }

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
    public void setStateBtnText(String caption) {
        stateBtn.setText(caption);
    }

    @Override
    public void showElements(boolean isAll) {
        parametersPanel.setVisible(isAll);
        typePanel.setVisible(isAll);
        if (isAll) {
            root.addStyleName("panel");
            namePanel.removeStyleName("col-xs-12");
            namePanel.addStyleName("col-xs-10");
        } else {
            root.removeStyleName("panel");
            namePanel.removeStyleName("col-xs-10");
            namePanel.addStyleName("col-xs-12");
        }
    }

    @UiHandler( "stateBtn" )
    public void onStateClicked (ClickEvent event)
    {
        if (activity != null) {
            activity.onStateChanged();
            activity.onSaveClicked();
        }
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
        setIsProduct(En_DevUnitType.PRODUCT.equals(event.getValue()));
    }

    private void checkName ()
    {
        setNameStatus(NameStatus.UNDEFINED);

        changeTimer.cancel();
        changeTimer.schedule(300);
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
    HTMLPanel namePanel;
    @UiField
    LabelElement nameLabel;
    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    ProductTypeBtnGroup type;
    @UiField
    HTMLPanel devUnitContainer;
    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector devUnits;
    @Inject
    @UiField(provided = true)
    ComponentMultiSelector components;
    @UiField
    Element verifiableIcon;
    @UiField
    TextArea info;
    @UiField
    Button saveBtn;
    @UiField
    Button cancelBtn;
    @UiField
    Button stateBtn;

    @Inject
    @UiField
    Lang lang;
    @Inject
    @UiField( provided = true )
    SubscriptionList subscriptions;
    @UiField
    MarkdownAreaWithPreview historyVersion;
    @UiField
    MarkdownAreaWithPreview configuration;
    @UiField
    MarkdownAreaWithPreview cdrDescription;
    @UiField
    TextBox wikiLink;
    @UiField
    HTMLPanel parametersPanel;
    @UiField
    HTMLPanel typePanel;
    @UiField
    HTMLPanel root;


    AbstractProductEditActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductEditView > {}
}