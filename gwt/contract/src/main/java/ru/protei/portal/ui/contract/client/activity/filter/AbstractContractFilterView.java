package ru.protei.portal.ui.contract.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Set;

public interface AbstractContractFilterView extends IsWidget {

    void setActivity(AbstractContractFilterActivity activity);

    void resetFilter();

    HasValue<String> searchString();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();
}
