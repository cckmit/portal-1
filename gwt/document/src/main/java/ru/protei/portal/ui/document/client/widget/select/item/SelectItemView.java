package ru.protei.portal.ui.document.client.widget.select.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * Один элемент инпут-селектора
 */
public class SelectItemView extends Composite implements AbstractSelectItemView, HasEnabled {
    public SelectItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractSelectItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setValue(String value) {
        curValue = value;
        text.setInnerText(value);
    }

    @Override
    public String getValue() {
        return curValue;
    }

    @UiHandler("close")
    public void onCloseClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onCloseClicked(this);
        }
    }

    @Override
    public boolean isEnabled() {
        return close.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        close.setEnabled(enabled);
        if (enabled) {
            close.removeStyleName("no-action");
        } else {
            close.addStyleName("no-action");
        }
    }

    @UiField
    DivElement text;

    @UiField
    Anchor close;
    String curValue = null;

    AbstractSelectItemActivity activity;

    interface SelectItemViewUiBinder extends UiBinder<HTMLPanel, SelectItemView> {
    }

    private static SelectItemViewUiBinder ourUiBinder = GWT.create(SelectItemViewUiBinder.class);

}