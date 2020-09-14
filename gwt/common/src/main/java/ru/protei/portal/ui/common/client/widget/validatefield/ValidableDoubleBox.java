package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;

import java.text.ParseException;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class ValidableDoubleBox extends ValidableValueBoxBase<Double> {

    public ValidableDoubleBox() {
        super(Document.get().createTextInputElement(), DoubleRenderer.instance(), DoubleParser.instance());
        setNotNull(false);
        setValidationFunction(Objects::nonNull);
    }

    public HandlerRegistration addInputHandler(InputHandler handler) {
        return addDomHandler(handler, InputEvent.getType());
    }


    private static class DoubleParser implements Parser<Double> {
        private static DoubleParser INSTANCE;
        public static Parser<Double> instance() {
            if (INSTANCE == null) {
                INSTANCE = new DoubleParser();
            }
            return INSTANCE;
        }

        public Double parse(CharSequence object) throws ParseException {
            try {
                String sValue = object.toString();
                if (isEmpty(sValue)) {
                    return null;
                }
                double dValue = Double.parseDouble(sValue.replace(",", "."));
                return dValue;
            } catch (NumberFormatException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
    }

    private static class DoubleRenderer extends AbstractRenderer<Double> {
        private static DoubleRenderer INSTANCE;
        public static Renderer<Double> instance() {
            if (INSTANCE == null) {
                INSTANCE = new DoubleRenderer();
            }
            return INSTANCE;
        }

        public String render(Double dValue) {
            if (dValue == null) {
                return "";
            }
            String sValue = Double.toString(dValue);
            return sValue;
        }
    }
}
