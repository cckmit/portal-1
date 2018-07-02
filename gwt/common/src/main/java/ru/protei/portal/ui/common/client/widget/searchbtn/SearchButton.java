package ru.protei.portal.ui.common.client.widget.searchbtn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.searchbtn.popup.SearchInputPopup;

public class SearchButton extends Composite implements HasValue<String> {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        this.value = value;
        popup.setValue(value, fireEvents);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @UiHandler("searchBtn")
    public void searchBtnClick(ClickEvent event) {
        event.preventDefault();
        showPopup();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void showPopup() {
        popup.showNear(searchBtn);
        popup.setValue(value);
        popup.addValueChangeHandler(event -> {
            value = event.getValue();
            ValueChangeEvent.fire(this, event.getValue());
        });
    }

    @Inject
    SearchInputPopup popup;
    @UiField
    Button searchBtn;

    private String value = "";

    interface SearchButtonViewUiBinder extends UiBinder<HTMLPanel, SearchButton> {}
    private static SearchButtonViewUiBinder ourUiBinder = GWT.create(SearchButtonViewUiBinder.class);
}
