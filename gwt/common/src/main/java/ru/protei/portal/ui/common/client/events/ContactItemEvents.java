package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.struct.ContactItem;

import java.util.List;

/**
 * События для активити
 */
public class ContactItemEvents {
    public static class ShowList {
        public ShowList(HasWidgets parent, List<ContactItem> data, List<En_ContactItemType> types, En_ContactDataAccess accessType) {
            this.parent = parent;
            this.data = data;
            this.types = types;
            this.accessType = accessType;
        }

        public HasWidgets parent;
        public List<ContactItem> data;
        public List<En_ContactItemType> types;
        public En_ContactDataAccess accessType;
    }
}
