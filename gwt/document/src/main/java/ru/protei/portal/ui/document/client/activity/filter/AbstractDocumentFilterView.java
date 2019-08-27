package ru.protei.portal.ui.document.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;
import java.util.Set;

public interface AbstractDocumentFilterView extends IsWidget {

    void setActivity(AbstractDocumentFilterActivity activity);

    void resetFilter();

    HasValue<String> name();

    HasValue<String> content();

    HasValue<PersonShortView> manager();

    HasValue<En_SortField> sortField();

    HasValue<Set<En_OrganizationCode>> organizationCodes();

    HasValue<DateInterval> dateRange();

    HasValue<DocumentType> documentType();

    HasValue<Boolean> approved();

    HasValue<List<String>> keywords();

    HasValue<Boolean> sortDir();

    HasValue<Boolean> showDeprecated();
}
