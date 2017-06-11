package ru.protei.portal.ui.role.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление создания и редактирования роли
 */
public interface AbstractRoleEditView extends IsWidget {
    void setActivity( AbstractRoleEditActivity activity );

    HasValue<String> name();

    HasValue<String> description();
}
