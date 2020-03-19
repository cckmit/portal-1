package ru.protei.portal.ui.education.client.activity.tableworker;

import ru.protei.portal.core.model.ent.EducationEntry;

public interface AbstractEducationTableWorkerActivity {

    void requestEntry();

    void requestAttendance(EducationEntry entry);
}
