package ru.protei.portal.ui.product.client.view.quickcreate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.product.client.activity.quickcreate.AbstractProductCreateActivity;
import ru.protei.portal.ui.product.client.activity.quickcreate.AbstractProductCreateView;

public class ProductCreateView extends Composite implements AbstractProductCreateView {

    public ProductCreateView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractProductCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() { return name; }

    @Override
    public HasValidable nameValidator() { return name; }

    @Override
    public HasValue<String> info() { return info; }

    @Override
    public void setNameStatus (NameStatus status)
    {
        verifiableIcon.setClassName(status.getStyle());
    }

    @UiHandler("name")
    public void onNameKeyUp (KeyUpEvent event) {
        checkName();
    }

    @UiHandler("name")
    public void onBlur (BlurEvent event) {
        checkName();
    }

    @UiHandler("saveBtn")
    public void onSaveClicked(ClickEvent event)
    {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event)
    {
        if (activity != null) {
            activity.onResetClicked();
        }
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
            if (activity != null) {
                activity.onNameChanged();
            }
        }
    };

    @UiField
    Element verifiableIcon;

    @UiField
    ValidableTextBox name;

    @UiField
    AutoResizeTextArea info;

    @UiField
    Button saveBtn;

    @UiField
    Button resetBtn;

    AbstractProductCreateActivity activity;

    private static ProductCreateViewUiBinder ourUiBinder = GWT.create( ProductCreateViewUiBinder.class );
    interface ProductCreateViewUiBinder extends UiBinder< HTMLPanel, ProductCreateView > {}
}