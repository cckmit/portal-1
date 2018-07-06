package ru.protei.portal.ui.sitefolder.client.activity.plaform.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractSiteFolderTableView extends IsWidget {

    void setActivity(AbstractSiteFolderTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void setPlatformsCount(Long count);

    int getPageSize();

    int getPageCount();

    void scrollTo(int page);

    void updateRow(Platform item);

    HasWidgets getPreviewContainer();

    HasWidgets getFilterContainer();
}
