package ru.protei.portal.ui.documentation.client.activity.table;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractDocumentationTableView extends IsWidget {
    void setActivity(AbstractDocumentationTableActivity documentTableActivity);

    void clearRecords();

    void setRecordCount(Long count);

    int getPageSize();

    int getPageCount();

    void scrollTo(int page);

    void setAnimation(TableAnimation animation);
}
