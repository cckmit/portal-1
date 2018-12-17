package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.Set;

public interface AbstractIssueReportCreateView extends IsWidget {

    void setActivity(AbstractIssueReportCreateActivity activity);

    HasValue<En_ReportType> reportType();
    HasValue<String> name();

    HasValue<Set<EntityOption>> companies();
    HasValue<Set<ProductShortView>> products();
    HasValue<Set<PersonShortView>> managers();
    HasValue<Set<PersonShortView>> initiators();
    HasValue<Set<En_CaseState>> states();
    HasValue<Set<En_ImportanceLevel>> importances();
    HasValue<DateInterval> dateRange();
    HasValue<En_SortField> sortField();
    HasValue<Boolean> sortDir();
    HasValue<String> searchPattern();
    HasValue<Boolean> searchPrivate();
    HasVisibility companiesVisibility();
    HasVisibility productsVisibility();
    HasVisibility managersVisibility();
    HasVisibility searchPrivateVisibility();

    HasValue<Set<PersonShortView>> commentAuthors();

    void resetFilter();
}
