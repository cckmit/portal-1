package ru.protei.portal.ui.common.client.widget.filterwidget;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

import java.util.function.Consumer;

public interface FilterParamView<Q extends FilterQuery> extends IsWidget {
    void resetFilter();

    Q getQuery();

    default void fillFilterFields(Q query, SelectorsParams filter){};

    default void setValidateCallback(Consumer<Boolean> callback){};

    void setOnFilterChangeCallback(Runnable callback);
}
