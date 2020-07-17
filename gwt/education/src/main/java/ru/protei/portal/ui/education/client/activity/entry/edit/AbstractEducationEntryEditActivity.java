package ru.protei.portal.ui.education.client.activity.entry.edit;

import ru.protei.portal.core.model.dict.EducationEntryType;

public interface AbstractEducationEntryEditActivity {

    void onTypeChanged(EducationEntryType type);

    void onSaveClicked();

    void onCloseClicked();
}
