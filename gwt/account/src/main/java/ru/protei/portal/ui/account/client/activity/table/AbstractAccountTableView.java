package ru.protei.portal.ui.account.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.List;

/**
 * Представление создания и редактирования учетной записи
 */
public interface AbstractAccountTableView extends IsWidget {
    void setActivity( AbstractAccountTableActivity activity );
    void setAnimation ( TableAnimation animation );

    void addRecords( List< UserLogin > accounts );
    void clearRecords();

    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    HasWidgets getPagerContainer();
}
