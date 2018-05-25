package ru.protei.portal.ui.issuereport.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.Set;

public interface AbstractIssueReportEditView extends IsWidget {

    void setActivity(AbstractIssueReportEditActivity activity);

    HasValue<String> title();
    HasValue<String> locale();
    HasText search();
    HasValue<DateInterval> dateRange();
    HasValue<En_SortField> sortField();
    HasValue<Boolean> sortDir();
    HasValue<Set<EntityOption>> companies();
    HasValue<Set<ProductShortView>> products();
    HasValue<Set<PersonShortView>> managers();
    HasValue<Set<En_ImportanceLevel>> importance();
    HasValue<Set<En_CaseState>> state();

    HasEnabled titleEnabled();
    HasEnabled localeEnabled();
    HasEnabled searchEnabled();
    HasEnabled dateRangeEnabled();
    HasEnabled sortFieldEnabled();
    HasEnabled sortDirEnabled();
    HasEnabled companiesEnabled();
    HasEnabled productsEnabled();
    HasEnabled managersEnabled();
    HasEnabled importanceEnabled();
    HasEnabled stateEnabled();

    HasVisibility companiesVisibility();
    HasVisibility productsVisibility();
    HasVisibility managersVisibility();

    HasVisibility requestButtonVisibility();

}
