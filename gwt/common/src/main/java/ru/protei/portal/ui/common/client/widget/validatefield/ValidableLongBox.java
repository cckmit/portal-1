package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.client.LongParser;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;
import ru.protei.portal.ui.common.client.render.LongNoSpaceRenderer;

import static ru.protei.portal.core.model.util.CrmConstants.Masks.ONLY_DIGITS;

public class ValidableLongBox extends ValidableValueBoxBase<Long> {

    public ValidableLongBox() {
        super(Document.get().createTextInputElement(), LongNoSpaceRenderer.instance(), LongParser.instance());
        setRegexp(ONLY_DIGITS);
    }

    public HandlerRegistration addInputHandler(InputHandler handler) {
        return addDomHandler(handler, InputEvent.getType());
    }
}
