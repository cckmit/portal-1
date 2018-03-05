package ru.protei.portal.ui.documentation.client.activity.table;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Documentation;

public interface AbstractDocumentationTableView extends IsWidget {
    void setActivity(AbstractDocumentationTableActivity documentTableActivity);

    void clearRecords();

    void addRow(Documentation documentation);

    void updateRow(Documentation documentation);

    int getPageSize();

    int getPageCount();

    void scrollTo(int page);
}
