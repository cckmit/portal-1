package ru.protei.portal.ui.common.client.widget.subscription.checkedlocale;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.ui.common.client.widget.optionlist.item.OptionItem;
import ru.protei.portal.ui.common.client.widget.subscription.locale.LocaleButtonSelector;

public class CheckedLocale extends Composite implements HasValue<NotificationEntry>, HasVisibility {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public NotificationEntry getValue() {
        return value;
    }

    @Override
    public void setValue(NotificationEntry value) {
        setValue(value, false);
    }

    @Override
    public void setValue(NotificationEntry value, boolean fireEvents) {
        this.value = value;
        option.setValue(value != null);
        locale.setVisible(value != null);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    public void setName(String value) {
        option.setName(value);
    }

    public void setLocale(String value) {
        locale.setValue(value);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<NotificationEntry> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("option")
    public void onChangeCompany(ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            value = NotificationEntry.email(null, locale.getValue());
        } else {
            value = null;
        }
        locale.setVisible(value != null);
        ValueChangeEvent.fire(this, value);
    }

    @UiField
    OptionItem option;
    @Inject
    @UiField(provided = true)
    LocaleButtonSelector locale;

    private NotificationEntry value = null;

    interface CheckedLocaleUiBinder extends UiBinder<HTMLPanel, CheckedLocale> {}
    private static CheckedLocaleUiBinder ourUiBinder = GWT.create(CheckedLocaleUiBinder.class);
}
