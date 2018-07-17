package ru.protei.portal.ui.sitefolder.client.activity.server.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;

public interface AbstractSiteFolderServerFilterView extends IsWidget {

    void setActivity(AbstractSiteFolderServerFilterActivity activity);

    void resetFilter();

    HasValue<String> name();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<String> ip();

    HasValue<String> parameters();

    HasValue<String> comment();
}
