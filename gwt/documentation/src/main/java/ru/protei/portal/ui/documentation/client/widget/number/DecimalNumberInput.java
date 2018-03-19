package ru.protei.portal.ui.documentation.client.widget.number;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.equipment.client.widget.number.list.DecimalNumberList;

import java.util.LinkedList;
import java.util.List;

public class DecimalNumberInput extends DecimalNumberList {
    @Override
    public void onAddPamrClicked(ClickEvent event) {
        if (getValue().isEmpty()) {
            super.onAddPamrClicked(event);
        }
    }

    @Override
    public void onAddPdraClicked(ClickEvent event) {
        if (getValue().isEmpty()) {
            super.onAddPdraClicked(event);
        }
    }

    public HasValue<DecimalNumber> singleHasValue() {
        return new HasValue<DecimalNumber>() {
            @Override
            public DecimalNumber getValue() {
                return getFirstOrNull(DecimalNumberInput.this.getValue());
            }

            @Override
            public void setValue(DecimalNumber value) {
                DecimalNumberInput.this.setValue(value == null ? null : listOf(value));
            }

            private <T> List<T> listOf(T val) {
                List<T> l = new LinkedList<>();
                l.add(val);
                return l;
            }

            @Override
            public void setValue(DecimalNumber value, boolean fireEvents) {
                DecimalNumberInput.this.setValue(listOf(value), fireEvents);
            }

            private <T> T getFirstOrNull(List<T> col) {
                if (col == null || col.isEmpty()) {
                    return null;
                }
                return col.get(0);
            }

            @Override
            public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DecimalNumber> handler) {
                return addHandler(handler, ValueChangeEvent.getType());
            }

            @Override
            public void fireEvent(GwtEvent<?> event) {
                DecimalNumberInput.this.fireEvent(event);
            }
        };
    }
}
