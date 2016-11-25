package ru.protei.portal.ui.common.client.activity.contactitem;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.struct.ContactItem;

import java.util.List;

/**
 * Модель элемента
 */
public class ContactItemModel {

    public ContactItemModel(HasWidgets parent, List<ContactItem> data, List<En_ContactItemType> allowedTypes, ContactItem ci){
        this.parent = parent;
        this.data = data;
        this.allowedTypes = allowedTypes;
        this.contactItem = ci;
    }

    public HasWidgets parent;
    public ContactItem contactItem;
    public List<ContactItem> data;
    public List<En_ContactItemType> allowedTypes;


}
