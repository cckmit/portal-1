package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;

/**
 * Сервис управления фильтрами обращений на DAO слое
 */
public interface IssueFilterService {

    CoreResponse<List<CaseFilterShortView>> getIssueFilterShortViewList(Long loginId, En_CaseFilterType filterType);

    CoreResponse<CaseFilter> getIssueFilter(Long id);

    CoreResponse<CaseFilter> saveIssueFilter(AuthToken token, CaseFilter filter);

    CoreResponse<Boolean> removeIssueFilter(Long id);
}
