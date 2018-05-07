package ru.protei.portal.ui.role.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;

import java.util.Set;

/**
 * Представление создания и редактирования роли
 */
public interface AbstractRoleEditView extends IsWidget {
    void setActivity( AbstractRoleEditActivity activity );

    HasValue<String> name();

    HasValue<String> description();

    HasValue<Set<En_Privilege>> privileges();

    HasValue< En_Scope > scope();

    HasValue<Boolean> defaultForContact();
}
