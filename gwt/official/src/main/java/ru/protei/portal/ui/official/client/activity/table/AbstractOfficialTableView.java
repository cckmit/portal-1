package ru.protei.portal.ui.official.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Created by serebryakov on 21/08/17.
 */
public interface AbstractOfficialTableView extends IsWidget {

    void setActivity( AbstractOfficialsTableActivity activity );
    void setAnimation ( TableAnimation animation );

    void hideElements();
    void showElements();
    void clearRecords();
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    int getPageSize();

    int getPageCount();

    void addSeparator(String text);

    void addRow(Official official);
}
