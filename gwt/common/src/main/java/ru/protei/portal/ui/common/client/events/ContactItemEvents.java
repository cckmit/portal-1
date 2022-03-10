package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactEmailSubscriptionType;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.ui.common.client.activity.contactitem.AbstractContactItemView;

import java.util.List;

/**
 * События для активити
 */
public class ContactItemEvents {
    public static class ShowList {

        public ShowList(HasWidgets parent, List<ContactItem> data, List<En_ContactItemType> types,
                        En_ContactDataAccess accessType, En_ContactEmailSubscriptionType subscriptionType, List<AbstractContactItemView> contactItemViews, String regexp) {
            this.parent = parent;
            this.data = data;
            this.types = types;
            this.accessType = accessType;
            this.subscriptionType = subscriptionType;
            this.contactItemViews = contactItemViews;
            this.regexp = regexp;
        }

        public HasWidgets parent;
        public List<ContactItem> data;
        public List<En_ContactItemType> types;
        public En_ContactDataAccess accessType;
        public En_ContactEmailSubscriptionType subscriptionType;
        public List<AbstractContactItemView> contactItemViews;
        public String regexp;
    }
}
