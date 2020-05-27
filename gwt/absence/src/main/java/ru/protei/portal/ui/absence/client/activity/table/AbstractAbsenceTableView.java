package ru.protei.portal.ui.absence.client.activity.table;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.List;

public interface AbstractAbsenceTableView extends IsWidget {
    void setActivity(AbstractAbsenceTableActivity activity);
    void clearRecords();
    void addRecords(List<PersonAbsence> absences);
    void showRemoveColumn(boolean isVisible);
}
