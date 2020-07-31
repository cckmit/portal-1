package ru.protei.portal.ui.education.client.activity.tableworker;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.EducationEntry;

import java.util.List;

public interface AbstractEducationTableWorkerView extends IsWidget {

    void setActivity(AbstractEducationTableWorkerActivity activity);

    void clearRecords();

    void putRecords(List<EducationEntry> list);

    void setTotalRecords(int totalRecords);

    void showLoader(boolean isShow);

    void showRequestEntryAction(boolean isShow);

    void showRequestAttendanceAction(boolean isShow);
}
