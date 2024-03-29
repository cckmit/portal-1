package ru.protei.portal.ui.common.client.activity.contactitem;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.List;

/**
 * Абстракция представления элемента списка
 */
public interface AbstractContactItemView extends IsWidget {
    void setActivity(AbstractContactItemActivity activity);

    HasText value();
    HasValidable valueValidator();
    HasValue<En_ContactItemType> type();
    HasVisibility typeVisibility();
    void fillTypeOptions(List<En_ContactItemType> options);
    void focused();
    void setRegexpValidation(String regexp);
}
