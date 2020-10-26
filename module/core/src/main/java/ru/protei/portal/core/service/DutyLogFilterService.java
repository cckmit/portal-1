package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DutyLogFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.FilterShortView;

import java.util.List;

public interface DutyLogFilterService {
    Result<List<FilterShortView>> getShortViewList(Long loginId);

    Result<DutyLogFilter> getFilter(AuthToken token, Long id );

    Result<SelectorsParams> getSelectorsParams(AuthToken token, DutyLogQuery query);

    Result<DutyLogFilter> saveFilter(AuthToken token, DutyLogFilter filter);

    Result<Long> removeFilter(Long id);
}
