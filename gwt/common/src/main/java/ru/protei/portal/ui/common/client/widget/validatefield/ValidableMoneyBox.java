package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;

import java.text.ParseException;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class ValidableMoneyBox extends ValidableValueBoxBase<Money> {

    public ValidableMoneyBox() {
        super(Document.get().createTextInputElement(), MoneyRenderer.instance(), MoneyParser.instance());
        setRegexp(CrmConstants.Masks.MONEY);
        setValidationFunction(Objects::nonNull);
        setPlaceholder("xxx.xx");
    }

    public HandlerRegistration addInputHandler(InputHandler handler) {
        return addDomHandler(handler, InputEvent.getType());
    }


    private static class MoneyParser implements Parser<Money> {
        private static MoneyParser INSTANCE;
        public static Parser<Money> instance() {
            if (INSTANCE == null) {
                INSTANCE = new MoneyParser();
            }
            return INSTANCE;
        }

        public Money parse(CharSequence object) throws ParseException {
            try {
                String sValue = object.toString();
                if (isEmpty(sValue)) {
                    return null;
                }
                double dValue = Double.parseDouble(sValue.replace(",", "."));
                long lValue = (long) (dValue * 100);
                return new Money(lValue);
            } catch (NumberFormatException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
    }

    private static class MoneyRenderer extends AbstractRenderer<Money> {
        private static MoneyRenderer INSTANCE;
        public static Renderer<Money> instance() {
            if (INSTANCE == null) {
                INSTANCE = new MoneyRenderer();
            }
            return INSTANCE;
        }

        public String render(Money value) {
            if (value == null) {
                return "";
            }
            long lValue = value.getFull();
            double dValue = ((double) lValue) / 100;
            String sValue = Double.toString(dValue);
            long decimal = value.getDecimal();
            if (decimal <= 0) {
                sValue += ".00";
            } else if (decimal >= 10 && decimal % 10 == 0) {
                sValue += "0";
            }
            return sValue;
        }
    }
}
