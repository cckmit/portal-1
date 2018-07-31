package ru.protei.portal.ui.product.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.subscription.list.SubscriptionList;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditActivity;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView;
import ru.protei.portal.ui.product.client.widget.type.ProductTypeBtnGroup;

import java.util.List;

/**
 * Вид карточки создания/редактирования продукта
 */
public class ProductEditView extends Composite implements AbstractProductEditView {

    @Inject
    public void onInit() { initWidget(ourUiBinder.createAndBindUi(this)); }

    @Override
    public void setActivity(AbstractProductEditActivity activity) {
        this.activity = activity;
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
    public HasValue<String> info() { return info; }

    @Override
    public HasVisibility state() { return stateBtn; }

    @Override
    public HasEnabled save() { return saveBtn; }

    @Override
    public void setNameStatus (NameStatus status)
    {
        verifiableIcon.setClassName(status.getStyle());
    }

    @Override
    public void setStateBtnText(String caption) {
        stateBtn.setText(caption);
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
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    ProductTypeBtnGroup type;
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


    AbstractProductEditActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductEditView > {}
}