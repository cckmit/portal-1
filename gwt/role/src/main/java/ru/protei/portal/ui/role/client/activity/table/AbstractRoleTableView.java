package ru.protei.portal.ui.role.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.List;

/**
 * Представление таблицы роли
 */
public interface AbstractRoleTableView extends IsWidget {

    void setActivity( AbstractRoleTableActivity activity );
    void setAnimation ( TableAnimation animation );

    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    void setData( List<UserRole> roles );

    void clearRecords();

    void clearSelection();
}
