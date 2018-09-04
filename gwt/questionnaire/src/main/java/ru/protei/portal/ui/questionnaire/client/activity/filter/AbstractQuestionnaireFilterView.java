package ru.protei.portal.ui.questionnaire.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Set;

public interface AbstractQuestionnaireFilterView extends IsWidget {

    void setActivity(AbstractQuestionnaireFilterActivity activity);

    void resetFilter();

    HasValue<String> searchString();

    HasValue<DateInterval> dateRange();

    HasValue<Set<En_CaseState>> states();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();
}
