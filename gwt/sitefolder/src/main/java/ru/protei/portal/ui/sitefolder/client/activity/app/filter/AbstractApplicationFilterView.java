package ru.protei.portal.ui.sitefolder.client.activity.app.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

public interface AbstractApplicationFilterView extends IsWidget {

    void setActivity(AbstractApplicationFilterActivity activity);

    void resetFilter();

    HasValue<String> name();

    HasValue<Set<EntityOption>> servers();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<String> comment();
}