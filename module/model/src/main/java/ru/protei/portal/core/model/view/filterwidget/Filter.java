package ru.protei.portal.core.model.view.filterwidget;

import ru.protei.portal.core.model.ent.SelectorsParams;

public interface Filter<FSV extends FilterShortView, Q extends FilterQuery> {
    Long getId();

    void setId(Long id);

    String getName();

    FSV toShortView();

    Q getQuery();

    SelectorsParams getSelectorsParams();
}
