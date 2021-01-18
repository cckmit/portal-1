package ru.protei.portal.ui.common.client.widget.contactitem.group;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import java.util.Objects;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class TextWithValidationItem extends ValidableTextBox
        implements HasCloseHandlers<TextWithValidationItem>, HasAddHandlers {

    public TextWithValidationItem() {
        super();
        this.addInputHandler(event -> {
            final String value = getValue();
            if ( isEmpty(value) ) {
                CloseEvent.fire( this, this );
            }

            if ( isEmpty(oldValue) && isNotEmpty(value) ) {
                AddEvent.fire( this );
            }

            oldValue = value;
        });
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<TextWithValidationItem> handler) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler( handler, AddEvent.getType() );
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        if ( isEmpty(value) ) {
            CloseEvent.fire( this, this );
        }

        if ( !Objects.equals(value, getValue()) ) {
            AddEvent.fire( this );
        }

        super.setValue(value, fireEvents);

        oldValue = value;
    }

    private String oldValue = null;
}
