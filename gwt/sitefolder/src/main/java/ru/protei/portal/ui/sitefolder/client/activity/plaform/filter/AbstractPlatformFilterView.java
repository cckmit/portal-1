package ru.protei.portal.ui.sitefolder.client.activity.plaform.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

public interface AbstractPlatformFilterView extends IsWidget {

    void setActivity(AbstractPlatformFilterActivity activity);

    void resetFilter();

    HasValue<String> name();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<Set<EntityOption>> companies();

    HasValue<String> parameters();

    HasValue<String> comment();
}
