package ru.protei.portal.ui.contract.client.activity.table;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface AbstractContractTableView extends IsWidget {
    void setActivity(AbstractContractTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    HasWidgets getPreviewContainer();

    HTMLPanel getFilterContainer();
}
