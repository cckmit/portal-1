package ru.protei.portal.ui.sitefolder.client.activity.plaform.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface AbstractPlatformTableView extends IsWidget {

    void setActivity(AbstractPlatformTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo(int page);

    void updateRow(Platform item);

    HasWidgets getPreviewContainer();

    HasWidgets getFilterContainer();

    HasWidgets getPagerContainer();
}
