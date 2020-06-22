package ru.protei.portal.ui.common.client.widget.typedrangepicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.ui.common.client.widget.selector.rangetype.RangeTypeButtonSelector;

import java.util.List;
import java.util.Objects;

public class TypedSelectorRangePicker extends Composite implements HasValue<DateIntervalWithType> {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        selector.addValueChangeHandler(new ValueChangeHandler<En_DateIntervalType>() {
            @Override
            public void onValueChange(ValueChangeEvent<En_DateIntervalType> event) {
                setRangePicker(event.getValue());
            }
        });
    }

    public void setHeader( String label ) {
        this.label.removeStyleName( "hide" );
        this.label.getElement().setInnerText( label == null ? "" : label );
    }

    private void setRangePicker(En_DateIntervalType type) {
        range.setVisible(Objects.equals(type, En_DateIntervalType.FIXED));
        range.setMandatory(Objects.equals(type, En_DateIntervalType.FIXED));
    }

    public void fillSelector(List<En_DateIntervalType> items ) {
        selector.fillOptions(items);
    }

    @Override
    public DateIntervalWithType getValue() {
        return new DateIntervalWithType(range.getValue(), selector.getValue());
    }

    @Override
    public void setValue(DateIntervalWithType value) {
        selector.setValue(value == null ? null : value.getIntervalType(), true);
        range.setValue(value == null ? null : value.getInterval());
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

    @UiField
    HTMLPanel label;

    @Inject
    @UiField(provided = true)
    RangeTypeButtonSelector selector;

    @Inject
    @UiField(provided = true)
    RangePicker range;

    interface TypedSelectorRangePickerUiBinder extends UiBinder<Widget, TypedSelectorRangePicker> { }
    private static TypedSelectorRangePickerUiBinder ourUiBinder = GWT.create(TypedSelectorRangePickerUiBinder.class);
}
