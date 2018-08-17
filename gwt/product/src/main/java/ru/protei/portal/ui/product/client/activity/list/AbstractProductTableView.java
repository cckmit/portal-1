package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.List;

/**
 * Created by bondarenko on 31.10.17.
 */
public interface AbstractProductTableView extends IsWidget {

    void setActivity( AbstractProductTableActivity activity );
    void setAnimation ( TableAnimation animation );
    void clearRecords();
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    void setProductsCount(Long issuesCount );

    int getPageSize();

    int getPageCount();

    void scrollTo( int page );

    void updateRow(DevUnit item);

    HasWidgets getPagerContainer();
}
