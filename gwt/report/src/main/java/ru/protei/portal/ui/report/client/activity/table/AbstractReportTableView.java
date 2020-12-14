package ru.protei.portal.ui.report.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.ImportanceLevel;

import java.util.List;

public interface AbstractReportTableView extends IsWidget {

    void setActivity(AbstractReportTableActivity activity);

    void clearRecords();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo(int page);

    void updateRow(ReportDto item);

    HasWidgets getPagerContainer();

    void setImportanceLevels(List<ImportanceLevel> importanceLevels);
}
