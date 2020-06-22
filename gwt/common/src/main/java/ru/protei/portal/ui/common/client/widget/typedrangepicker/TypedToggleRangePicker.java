package ru.protei.portal.ui.common.client.widget.typedrangepicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.ui.common.client.lang.En_DateIntervalLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

import java.util.Objects;

public class TypedToggleRangePicker extends Composite implements HasValue<DateIntervalWithType> {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        btnGroup.addValueChangeHandler(new ValueChangeHandler<En_DateIntervalType>() {
            @Override
            public void onValueChange(ValueChangeEvent<En_DateIntervalType> event) {
                setRangePicker(event.getValue());
            }
        });
    }

    private void setRangePicker(En_DateIntervalType type) {
        range.setVisible(Objects.equals(type, En_DateIntervalType.FIXED));
        range.setMandatory(Objects.equals(type, En_DateIntervalType.FIXED));
    }

    public void addBtn(En_DateIntervalType value, String buttonStyle ) {
        btnGroup.addBtn(lang.getName(value), value,buttonStyle);
    }

    @Override
    public DateIntervalWithType getValue() {
        return new DateIntervalWithType(range.getValue(), btnGroup.getValue());
    }

    @Override
    public void setValue(DateIntervalWithType value) {
        btnGroup.setValue(value.getIntervalType(), true);
        range.setValue(value.getInterval());
    }

    @Override
    public void setValue(DateIntervalWithType value, boolean b) {
        setValue(value);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateIntervalWithType> valueChangeHandler) {
        return addHandler( valueChangeHandler, ValueChangeEvent.getType() );
    }

    public void setFormatValue(String value) { range.setFormatValue(value); }

    public void setRangeMandatory(boolean value) { range.setMandatory(value); }

    public void setEnableBtn(En_DateIntervalType intervalType, boolean value) {
        btnGroup.itemViewToModel.entrySet().stream()
                .filter(e -> Objects.equals(e.getValue(), intervalType))
                .findFirst().get().getKey().setEnabled(value);
    }

    @UiField
    ToggleBtnGroup<En_DateIntervalType> btnGroup;

    @Inject
    @UiField(provided = true)
    RangePicker range;

    @Inject
    En_DateIntervalLang lang;

    interface TypedToggleRangePickerUiBinder extends UiBinder<Widget, TypedToggleRangePicker> { }
    private static TypedToggleRangePickerUiBinder ourUiBinder = GWT.create(TypedToggleRangePickerUiBinder.class);
}
