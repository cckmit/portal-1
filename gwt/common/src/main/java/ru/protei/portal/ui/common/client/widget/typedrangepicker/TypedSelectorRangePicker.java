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
import ru.protei.portal.ui.common.client.widget.selector.rangetype.RangeTypeButtonSelector;

import java.util.List;
import java.util.Objects;

public class TypedSelectorRangePicker extends Composite implements HasValue<DateIntervalWithType> {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setHeader( String label ) {
        this.label.removeStyleName( "hide" );
        this.label.getElement().setInnerText( label == null ? "" : label );
    }

    public void fillSelector(List<En_DateIntervalType> items ) {
        rangeType.fillOptions(items);
    }

    @Override
    public DateIntervalWithType getValue() {
        return new DateIntervalWithType(range.getValue(), rangeType.getValue());
    }

    @Override
    public void setValue(DateIntervalWithType value) {
        setValue(value, false);
    }

    @Override
    public void setValue(DateIntervalWithType value, boolean fireEvents) {
        rangeType.setValue(value == null ? null : value.getIntervalType());
        range.setValue(value == null ? null : value.getInterval());
        setRangePicker(value == null ? null : value.getIntervalType());
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateIntervalWithType> valueChangeHandler) {
        return addHandler( valueChangeHandler, ValueChangeEvent.getType() );
    }

    @UiHandler("rangeType")
    void onChangeRangeType(ValueChangeEvent<En_DateIntervalType> event) {
        setRangePicker(event.getValue());
        ValueChangeEvent.fire(this, this.getValue());
   }

    @UiHandler("range")
    void onChangeRange(ValueChangeEvent<DateInterval> event) {
        ValueChangeEvent.fire(this, this.getValue());
    }

    public void setFormatValue(String value) { range.setFormatValue(value); }

    public void setRangeMandatory(boolean value) { range.setMandatory(value); }

    public void setEnsureDebugId(String debugId) {
        root.ensureDebugId(debugId);
    }

    private void setRangePicker(En_DateIntervalType type) {
        boolean isRangeAllowAndMandatory = type != null && Objects.equals(type, En_DateIntervalType.FIXED);
        range.setVisible(isRangeAllowAndMandatory);
        range.setMandatory(isRangeAllowAndMandatory);
    }

    @UiField
    HTMLPanel label;

    @Inject
    @UiField(provided = true)
    RangeTypeButtonSelector rangeType;

    @Inject
    @UiField(provided = true)
    RangePicker range;

    @UiField
    HTMLPanel root;

    interface TypedSelectorRangePickerUiBinder extends UiBinder<Widget, TypedSelectorRangePicker> { }
    private static TypedSelectorRangePickerUiBinder ourUiBinder = GWT.create(TypedSelectorRangePickerUiBinder.class);
}
