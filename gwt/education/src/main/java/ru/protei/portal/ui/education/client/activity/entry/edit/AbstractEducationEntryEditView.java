package ru.protei.portal.ui.education.client.activity.entry.edit;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.EducationEntryType;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.education.client.model.Approve;

import java.util.Map;
import java.util.Set;

public interface AbstractEducationEntryEditView extends IsWidget {

    void setActivity(AbstractEducationEntryEditActivity activity);

    HasValue<String> title();

    HasValue<EducationEntryType> type();

    HasValue<String> coins();

    HasValue<String> link();

    HasValue<String> location();

    HasValue<DateInterval> dates();

    HasValue<String> description();

    TakesValue<String> image();

    HasValue<Set<WorkerEntryShortView>> participants();

    HasValue<Map<EducationEntryAttendance, Approve>> attendance();

    void setTitleRequired(boolean isRequired);

    void setTypeRequired(boolean isRequired);

    void setCoinsRequired(boolean isRequired);

    void setLinkRequired(boolean isRequired);

    void setLocationRequired(boolean isRequired);

    void setDatesRequired(boolean isRequired);

    void setDescriptionRequired(boolean isRequired);

    void setImageRequired(boolean isRequired);

    void setParticipantsRequired(boolean isRequired);

    HasEnabled typeEnabled();

    HasVisibility participantsVisibility();

    HasVisibility attendanceVisibility();
}
