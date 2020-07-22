package ru.protei.portal.ui.common.client.widget.filterwidget;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

public interface FilterParamView<Q extends FilterQuery> extends IsWidget {
    void watchForScrollOf(Widget widget);

    void stopWatchForScrollOf(Widget widget);

    void resetFilter();

    Q getQuery();

    void fillFilterFields(Q query, SelectorsParams filter);
}
