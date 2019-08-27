package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;

/**
 * Сервис управления фильтрами обращений на DAO слое
 */
public interface IssueFilterService {

    Result<List<CaseFilterShortView>> getIssueFilterShortViewList( Long loginId, En_CaseFilterType filterType);

    Result<CaseFilter> getIssueFilter( Long id);

    Result<CaseFilter> saveIssueFilter( AuthToken token, CaseFilter filter);

    Result<Boolean> removeIssueFilter( Long id);
}
