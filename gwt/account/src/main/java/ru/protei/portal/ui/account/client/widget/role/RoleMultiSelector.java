package ru.protei.portal.ui.account.client.widget.role;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

/**
 * Мультиселектор ролей
 */
public class RoleMultiSelector
        extends InputPopupMultiSelector<UserRole> {

    @Inject
    public void init(AsyncRoleModel model, Lang lang) {
        setAsyncSearchModel(model);
        setAddName(lang.roleAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(UserRole::getCode);
    }
}
