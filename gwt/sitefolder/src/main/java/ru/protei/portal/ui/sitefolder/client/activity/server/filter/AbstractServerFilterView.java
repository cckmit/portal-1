package ru.protei.portal.ui.sitefolder.client.activity.server.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;

import java.util.Set;

public interface AbstractServerFilterView extends IsWidget {

    void setActivity(AbstractServerFilterActivity activity);

    void resetFilter();

    HasValue<String> name();

    HasValue<Set<EntityOption>> companies();

    HasValue<Set<PlatformOption>> platforms();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<String> ip();

    HasValue<String> parameters();

    HasValue<String> comment();
}
