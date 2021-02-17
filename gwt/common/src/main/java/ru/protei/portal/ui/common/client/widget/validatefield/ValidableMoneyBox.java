package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.text.shared.Parser;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.common.MoneyRenderer;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;

import java.text.ParseException;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class ValidableMoneyBox extends ValidableValueBoxBase<Money> {

    public ValidableMoneyBox() {
        super(Document.get().createTextInputElement(), MoneyRenderer.getInstance(), MoneyParser.instance());
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
                double dValue = NumberFormat.getDecimalFormat().parse(sValue);
                long lValue = (long) (dValue * 100);
                return new Money(lValue);
            } catch (NumberFormatException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }
    }
}
