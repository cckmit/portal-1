package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;

import java.util.function.Function;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class ValidableContactItemBox extends ValidableValueBoxBase<ContactItem> {
    public ValidableContactItemBox() {
        super(Document.get().createTextInputElement(),
                ContactItemRenderer.instance(), ContactItemParser.instance());
    }

    public HandlerRegistration addInputHandler(InputHandler handler) {
        return addDomHandler(handler, InputEvent.getType());
    }

    @Override
    public void setValue(ContactItem value) {
        setValue(value, false);
    }

    @Override
    public void setValue(ContactItem value, boolean fireEvents) {
        super.setValue(value, fireEvents);
        this.contactItem = value;
    }

    @Override
    public ContactItem getValue() {
        final ContactItem value = super.getValue();
        if (value == null) {
            return null;
        }
        if (contactItem == null) {
            return setTypeAndAccess.apply(value);
        } else {
            return contactItem.modify(value.value());
        }
    }

    public void setSetTypeAndAccess(Function<ContactItem, ContactItem> setTypeAndAccess) {
        this.setTypeAndAccess = setTypeAndAccess;
    }

    private Function<ContactItem, ContactItem> setTypeAndAccess;
    private ContactItem contactItem;

    private static class ContactItemParser implements Parser<ContactItem> {
        private static ContactItemParser INSTANCE;
        public static Parser<ContactItem> instance() {
            if (INSTANCE == null) {
                INSTANCE = new ContactItemParser();
            }
            return INSTANCE;
        }

        public ContactItem parse(CharSequence object) {
            String sValue = object.toString();
            if (isEmpty(sValue)) {
                return null;
            }

            return new ContactItem().modify(sValue);
        }
    }

    private static class ContactItemRenderer extends AbstractRenderer<ContactItem> {
        private static ContactItemRenderer INSTANCE;
        public static Renderer<ContactItem> instance() {
            if (INSTANCE == null) {
                INSTANCE = new ContactItemRenderer();
            }
            return INSTANCE;
        }

        public String render(ContactItem value) {
            if (value == null) {
                return "";
            }
            return value.value();
        }
    }
}
