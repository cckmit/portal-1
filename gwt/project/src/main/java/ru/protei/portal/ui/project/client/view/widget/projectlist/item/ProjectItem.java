package ru.protei.portal.ui.project.client.view.widget.projectlist.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.common.ClickHTMLPanel;

public class ProjectItem
        extends Composite
        implements HasValue<Boolean> {

    public ProjectItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public Boolean getValue() {
        return radio.getValue();
    }

    @Override
    public void setValue(Boolean value) {
        radio.setValue(value);
        if (value) {
            root.addStyleName("selected");
        } else {
            root.removeStyleName("selected");
        }
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        radio.setValue(value, fireEvents);
    }

    public void setCreated(String created) {
        this.created.setText(created == null ? "" : created);
    }

    public void setName( String name ) {
        this.name.setText( name == null ? "" : name );
    }

    public void setProducts(String products) {
        this.products.setText(products == null ? "" : products);
    }

    public void setCustomerType(String customerType) {
        this.customerType.setText(customerType == null ? "" : customerType);
    }

    public void setManagers(String managers) {
        this.managers.setText(managers == null ? "" : managers);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("radio")
    public void onRadioButtonClicked(ValueChangeEvent<Boolean> event) {
        ValueChangeEvent.fire(this, event.getValue());

        radio.setValue(event.getValue(), false);
    }

    @UiHandler("root")
    public void onItemClicked(ClickEvent event) {
        radio.setValue(!radio.getValue());
        ValueChangeEvent.fire(this, radio.getValue());
        if (radio.getValue()) {
            root.addStyleName("selected");
        } else {
            root.removeStyleName("selected");
        }
    }

    @UiField
    RadioButton radio;
    @UiField
    Label created;
    @UiField
    Label name;
    @UiField
    Label products;
    @UiField
    Label customerType;
    @UiField
    Label managers;
    @UiField
    ClickHTMLPanel root;

    private static ProjectItemUiBinder ourUiBinder = GWT.create(ProjectItemUiBinder.class);
    interface ProjectItemUiBinder extends UiBinder<HTMLPanel, ProjectItem> {}
}