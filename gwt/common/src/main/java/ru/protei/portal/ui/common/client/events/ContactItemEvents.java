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
        public ShowList(HasWidgets parent, List<ContactItem> data, List<En_ContactItemType> types, List<En_ContactDataAccess> accessList) {
            this.parent = parent;
            this.data = data;
            this.types = types;
            this.accessList = accessList;
        }

        public HasWidgets parent;
        public List<ContactItem> data;
        public List<En_ContactItemType> types;
        public List<En_ContactDataAccess> accessList;
    }
}
