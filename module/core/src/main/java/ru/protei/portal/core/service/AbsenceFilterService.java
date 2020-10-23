package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.AbsenceFilterShortView;

import java.util.List;

public interface AbsenceFilterService {

    Result<List<AbsenceFilterShortView>> getShortViewList(Long loginId);

    Result<AbsenceFilter> getFilter(AuthToken token, Long id );

    Result<SelectorsParams> getSelectorsParams(AuthToken token, AbsenceQuery query);

    Result<AbsenceFilter> saveFilter(AuthToken token, AbsenceFilter filter);

    Result<Long> removeFilter(Long id);
}
