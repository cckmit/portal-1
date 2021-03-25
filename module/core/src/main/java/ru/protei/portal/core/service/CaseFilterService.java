package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;

import java.util.List;

/**
 * Сервис управления фильтрами обращений на DAO слое
 */
public interface CaseFilterService {

    Result<List<AbstractFilterShortView>> getCaseFilterShortViewList(Long loginId, En_CaseFilterType filterType);

    <T extends HasFilterQueryIds> Result<CaseFilterDto<T>> getCaseFilterDto(AuthToken token, Long id );

    Result<SelectorsParams> getSelectorsParams( AuthToken token, HasFilterQueryIds filterEntityIds );

    <T extends HasFilterQueryIds> Result<CaseFilterDto<T>> saveCaseFilter(AuthToken token, CaseFilterDto<T> caseFilterDto);

    Result<Long> removeCaseFilter(AuthToken token, Long id);
}
