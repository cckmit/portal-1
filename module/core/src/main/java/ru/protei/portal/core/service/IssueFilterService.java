package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;

/**
 * Сервис управления фильтрами обращений на DAO слое
 */
public interface IssueFilterService {

    CoreResponse<List<CaseFilterShortView >> getIssueFilterShortViewList( Long loginId );

    CoreResponse<CaseFilter > getIssueFilter( Long id );

    CoreResponse<CaseFilter > saveIssueFilter( CaseFilter filter );

    CoreResponse<Boolean> removeIssueFilter( Long id );
}
