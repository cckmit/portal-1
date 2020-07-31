package ru.protei.portal.ui.education.client.activity.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

import java.util.List;

public interface AbstractEducationAdminActivity extends
        EditClickColumn.EditHandler<EducationEntry>,
        InfiniteLoadHandler<EducationEntry>, InfiniteTableWidget.PagerListener {
    void onEditClicked(EducationEntry entry);
    void loadData(int offset, int limit, AsyncCallback<List<EducationEntry>> handler);
    void onPageChanged(int pageNumber);
}
