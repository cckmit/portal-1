package ru.protei.portal.ui.common.client.widget.filterwidget;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

public interface AbstractFilterWidget<Q extends FilterQuery> extends IsWidget {
    void resetFilter();
    FilterParamView<Q> getFilterParamView();
    void setOnFilterChangeCallback(Runnable callback);
}
