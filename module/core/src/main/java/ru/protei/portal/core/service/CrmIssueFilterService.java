package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.IssueFilter;
import ru.protei.portal.core.model.view.IssueFilterShortView;

import java.util.List;

/**
 * Сервис управления фильтрами обращений на DAO слое
 */
public interface CrmIssueFilterService {

    CoreResponse<List<IssueFilterShortView>> getIssueFilterShortViewList( Long loginId );

    CoreResponse<IssueFilter> getIssueFilter( Long id );

    CoreResponse<IssueFilter> saveIssueFilter( IssueFilter filter );

    CoreResponse<Boolean> removeIssueFilter( Long id );
}
