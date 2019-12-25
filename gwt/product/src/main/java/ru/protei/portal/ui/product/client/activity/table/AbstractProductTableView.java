package ru.protei.portal.ui.product.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Created by bondarenko on 31.10.17.
 */
public interface AbstractProductTableView extends IsWidget {

    void setActivity( AbstractProductTableActivity activity );
    void setAnimation ( TableAnimation animation );
    void clearRecords();
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo( int page );

    void updateRow(DevUnit item);

    HasWidgets getPagerContainer();

    void clearSelection();
}
