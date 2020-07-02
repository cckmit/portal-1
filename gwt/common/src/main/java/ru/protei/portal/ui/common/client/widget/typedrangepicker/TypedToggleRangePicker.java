package ru.protei.portal.ui.common.client.widget.typedrangepicker;

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
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.ui.common.client.lang.En_DateIntervalLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

import java.util.Objects;

public class TypedToggleRangePicker extends Composite implements HasValue<DateIntervalWithType> {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void addBtn(En_DateIntervalType value, String buttonStyle ) {
        btnGroup.addBtn(lang.getName(value), value, buttonStyle);
    }

    @Override
    public DateIntervalWithType getValue() {
        return new DateIntervalWithType(range.getValue(), btnGroup.getValue());
    }

    @Override
    public void setValue(DateIntervalWithType value) {
        setValue(value, false);
    }

    @Override
    public void setValue(DateIntervalWithType value, boolean fireEvents) {
        btnGroup.setValue(value == null ? DEFAULT_TYPE : value.getIntervalType());
        range.setValue(value == null ? null : value.getInterval());
        changeRangePicker(value == null ? null : value.getIntervalType());
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateIntervalWithType> valueChangeHandler) {
        return addHandler( valueChangeHandler, ValueChangeEvent.getType() );
    }

    @UiHandler("btnGroup")
    void onChangeRangeType(ValueChangeEvent<En_DateIntervalType> event) {
        changeRangePicker(event.getValue());
        ValueChangeEvent.fire(this, this.getValue());
    }

    @UiHandler("range")
    void onChangeRange(ValueChangeEvent<DateInterval> event) {
        ValueChangeEvent.fire(this, this.getValue());
    }

    public void setFormatValue(String value) { range.setFormatValue(value); }

    public void setRangeMandatory(boolean value) { range.setMandatory(value); }

    public void setEnableBtn(En_DateIntervalType intervalType, boolean value) {
        btnGroup.itemViewToModel.entrySet().stream()
                .filter(e -> Objects.equals(e.getValue(), intervalType))
                .findFirst().get().getKey().setEnabled(value);
    }

    public void setEnsureDebugId(String debugId) {
        root.ensureDebugId(debugId);
    }

    private void changeRangePicker(En_DateIntervalType type) {
        boolean isRangeAllowAndMandatory = type != null && Objects.equals(type, En_DateIntervalType.FIXED);
        range.setVisible(isRangeAllowAndMandatory);
        range.setMandatory(isRangeAllowAndMandatory);
    }

    @UiField
    ToggleBtnGroup<En_DateIntervalType> btnGroup;

    @Inject
    @UiField(provided = true)
    RangePicker range;

    @Inject
    En_DateIntervalLang lang;

    @UiField
    HTMLPanel root;

    En_DateIntervalType DEFAULT_TYPE = En_DateIntervalType.THIS_MONTH;

    interface TypedToggleRangePickerUiBinder extends UiBinder<Widget, TypedToggleRangePicker> { }
    private static TypedToggleRangePickerUiBinder ourUiBinder = GWT.create(TypedToggleRangePickerUiBinder.class);
}
