package ru.protei.portal.ui.absence.client.widget.datetime.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.ui.absence.client.widget.datetime.HasVaryAbility;

public class AbsenceDatesItem extends Composite implements HasValue<DateInterval>, HasEnabled, HasVaryAbility {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setRemoveHandler(Runnable removeHandler) {
        this.removeHandler = removeHandler;
    }

    public void setChangeHandler(Runnable changeHandler) {
        this.changeHandler = changeHandler;
    }

    @Override
    public DateInterval getValue() {
        return dateRange.getValue();
    }

    @Override
    public void setValue(DateInterval value) {
        setValue(value, false);
    }

    @Override
    public void setValue(DateInterval value, boolean fireEvents) {
        dateRange.setValue(value, fireEvents);
    }

    @Override
    public boolean isEnabled() {
        return dateRange.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        dateRange.setEnabled(enabled);
    }

    @Override
    public boolean isVaryAble() {
        return remove.isEnabled();
    }

    @Override
    public void setVaryAble(boolean isVaryAble) {
        remove.setEnabled(isVaryAble);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateInterval> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateInterval> event) {
        if (changeHandler != null) {
            changeHandler.run();
        }
    }

    @UiHandler("remove")
    public void removeClick(ClickEvent event) {
        if (removeHandler != null) {
            removeHandler.run();
        }
    }

    @Inject
    @UiField(provided = true)
    RangePicker dateRange;
    @UiField
    Button remove;

    private Runnable removeHandler;
    private Runnable changeHandler;

    interface AbsenceDatesItemBinder extends UiBinder<HTMLPanel, AbsenceDatesItem> {}
    private static AbsenceDatesItemBinder ourUiBinder = GWT.create(AbsenceDatesItemBinder.class);
}
