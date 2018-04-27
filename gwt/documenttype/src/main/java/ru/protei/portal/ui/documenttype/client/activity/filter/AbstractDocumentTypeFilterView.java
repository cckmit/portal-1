package ru.protei.portal.ui.documenttype.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Set;

public interface AbstractDocumentTypeFilterView extends IsWidget {

    void setActivity(AbstractDocumentTypeFilterActivity activity);

    void resetFilter();

    HasValue<String> name();

    HasValue<En_SortField> sortField();

    HasValue<Set<En_DocumentCategory>> documentCategories();

    HasValue<Boolean> sortDir();
}
