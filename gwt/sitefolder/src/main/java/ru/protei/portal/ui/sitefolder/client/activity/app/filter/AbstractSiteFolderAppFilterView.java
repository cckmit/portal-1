package ru.protei.portal.ui.sitefolder.client.activity.app.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;

public interface AbstractSiteFolderAppFilterView extends IsWidget {

    void setActivity(AbstractSiteFolderAppFilterActivity activity);

    void resetFilter();

    HasValue<String> name();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<String> comment();
}