package ru.protei.portal.ui.document.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractDocumentTableView extends IsWidget {
    void setActivity(AbstractDocumentTableActivity documentTableActivity);

    void clearRecords();

    void setRecordCount(Long count);

    int getPageSize();

    int getPageCount();

    void scrollTo(int page);

    void setAnimation(TableAnimation animation);

    HasWidgets getFilterContainer();
}
