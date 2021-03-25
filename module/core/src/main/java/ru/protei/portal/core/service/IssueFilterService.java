package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.HasFilterEntityIds;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;
import ru.protei.portal.core.model.view.filterwidget.DtoFilterQuery;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

import java.util.List;

/**
 * Сервис управления фильтрами обращений на DAO слое
 */
public interface IssueFilterService {

    Result<List<AbstractFilterShortView>> getIssueFilterShortViewList(Long loginId, En_CaseFilterType filterType);

    <T extends DtoFilterQuery> Result<CaseFilterDto<T>> getIssueFilter(AuthToken token, Long id );

    Result<SelectorsParams> getSelectorsParams( AuthToken token, HasFilterEntityIds filterEntityIds );

    <T extends DtoFilterQuery> Result<CaseFilterDto<T>> saveIssueFilter(AuthToken token, CaseFilterDto<T> caseFilterDto);

    Result<Long> removeIssueFilter(AuthToken token, Long id);
}
