package ru.protei.portal.ui.product.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditActivity;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView;

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

    @Override
    public void reset() {

        name.setText("");
        info.setText("");
        state.setValue(true);

        name.setFocus(true);
    }

    @Override
    public void setName(String name) {
        this.name.setText(name);
    }

    @Override
    public HasText getName() {
        return name;
    }

    @Override
    public void setInfo(String info) {
        this.info.setText( info );
    }

    @Override
    public HasText getInfo() {
        return info;
    }

    @Override
    public void setState(boolean state) {

    }

    @Override
    public HasValue<Boolean> getState() {
        return null;
    }


    @UiField
    TextBox name;
    @UiField
    TextArea info;
    @UiField
    Button saveBtn;
    @UiField
    Button cancelBtn;

    ToggleButton state;

    AbstractProductEditActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductEditView > {}
}