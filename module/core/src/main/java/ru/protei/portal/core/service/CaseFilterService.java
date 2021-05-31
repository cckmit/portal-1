package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.FilterShortView;

import java.util.List;

/**
 * Сервис управления фильтрами обращений на DAO слое
 */
public interface CaseFilterService {

    Result<List<FilterShortView>> getCaseFilterShortViewList(Long loginId, En_CaseFilterType filterType);

    Result<CaseFilterDto<HasFilterQueryIds>> getCaseFilterDto(AuthToken token, Long id );

    Result<SelectorsParams> getSelectorsParams( AuthToken token, HasFilterQueryIds filterEntityIds );

    Result<CaseFilterDto<ProjectQuery>> saveProjectFilter(AuthToken token, CaseFilterDto<ProjectQuery> caseFilterDto);

    Result<CaseFilterDto<DeliveryQuery>> saveDeliveryFilter(AuthToken token, CaseFilterDto<DeliveryQuery> caseFilterDto);

    Result<CaseFilterDto<CaseQuery>> saveIssueFilter(AuthToken token, CaseFilterDto<CaseQuery> caseFilterDto);

    Result<Long> removeCaseFilter(AuthToken token, Long id);
}
