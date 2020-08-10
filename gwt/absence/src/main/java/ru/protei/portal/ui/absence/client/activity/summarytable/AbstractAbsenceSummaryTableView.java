package ru.protei.portal.ui.absence.client.activity.summarytable;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.absence.client.widget.AbsenceFilterWidget;

import java.util.List;

public interface AbstractAbsenceSummaryTableView extends IsWidget {
    void setActivity(AbstractAbsenceSummaryTableActivity activity);
    AbsenceFilterWidget getFilterWidget();
    void clearRecords();
    void addRecords(List<PersonAbsence> absences);
}