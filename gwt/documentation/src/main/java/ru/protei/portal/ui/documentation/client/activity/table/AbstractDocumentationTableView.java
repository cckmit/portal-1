package ru.protei.portal.ui.documentation.client.activity.table;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractDocumentationTableView extends IsWidget {
    void setActivity(AbstractDocumentationTableActivity documentTableActivity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void addRow(Documentation documentation);

    void updateRow(Documentation documentation);

    int getPageSize();
}
