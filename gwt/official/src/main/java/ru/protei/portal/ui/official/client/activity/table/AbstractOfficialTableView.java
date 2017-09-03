package ru.protei.portal.ui.official.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Абстрактное представление таблицы матриц принятий решений
 */
public interface AbstractOfficialTableView extends IsWidget {

    void setActivity( AbstractOfficialTableActivity activity );
    void setAnimation ( TableAnimation animation );

    void clearRecords();
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    void addSeparator(String text);

    void addRow(Official official);
}
