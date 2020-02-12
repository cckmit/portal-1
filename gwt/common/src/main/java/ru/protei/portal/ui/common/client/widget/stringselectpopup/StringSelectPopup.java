package ru.protei.portal.ui.common.client.widget.stringselectpopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.popup.BasePopupView;

import java.util.List;

public class StringSelectPopup extends BasePopupView implements HasValueChangeHandlers<String> {

    public StringSelectPopup() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
    }

    @Override
    protected UIObject getPositionRoot() {
        return root;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setValues(List<String> values) {
        childContainer.clear();
        for (String value : values) {
            Widget item = makeItemView(value);
            childContainer.add(item);
        }
    }

    private Widget makeItemView(String value) {
        Anchor item = GWT.create(Anchor.class);
        item.setStyleName("dropdown-item btn btn-default no-border bg-transparent");
        item.setText(value);
        item.addClickHandler(event -> {
            event.preventDefault();
            ValueChangeEvent.fire(this, value);
            hide();
        });
        return item;
    }

    @UiField
    HTMLPanel root;
    @UiField
    HTMLPanel dropdown;
    @UiField
    HTMLPanel childContainer;

    interface StringSelectPopupUiBinder extends UiBinder<HTMLPanel, StringSelectPopup> {}
    private static StringSelectPopupUiBinder ourUiBinder = GWT.create(StringSelectPopupUiBinder.class);
}
