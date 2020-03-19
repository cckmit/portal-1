package ru.protei.portal.ui.education.client.activity.entry.edit;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.EducationEntryType;

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

    HasVisibility declineButtonVisibility();

    HasVisibility approveButtonVisibility();

    HasVisibility saveButtonVisibility();

    HasEnabled approveButtonEnabled();
}
