package ru.protei.portal.ui.document.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface AbstractDocumentTableView extends IsWidget {
    void setActivity(AbstractDocumentTableActivity documentTableActivity);

    void clearRecords();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo(int page);

    void setAnimation(TableAnimation animation);

    HasWidgets getFilterContainer();

    HasWidgets getPreviewContainer();

    HasWidgets getPagerContainer();

    void clearSelection();
}
