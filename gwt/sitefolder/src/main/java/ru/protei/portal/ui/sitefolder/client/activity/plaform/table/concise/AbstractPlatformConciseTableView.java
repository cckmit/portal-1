package ru.protei.portal.ui.sitefolder.client.activity.plaform.table.concise;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Platform;

import java.util.List;

/**
 * Представление таблицы платформ
 */
public interface AbstractPlatformConciseTableView extends IsWidget {

    void setActivity(AbstractPlatformConciseTableActivity activity);

    void clearRecords();

    void setData(List<Platform> persons);
}
