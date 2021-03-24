package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления фильтрами обращений
 */
@RemoteServiceRelativePath( "springGwtServices/IssueFilterController" )
public interface IssueFilterController extends RemoteService {

    /**
     * Получение списка сокращенного представления CaseFilter
     */
    List< CaseFilterShortView > getIssueFilterShortViewList( En_CaseFilterType filterType ) throws RequestFailedException;

    CaseFilterDto<CaseQuery> getIssueFilter(Long id ) throws RequestFailedException;

    SelectorsParams getSelectorsParams( CaseQuery caseQuery ) throws RequestFailedException;

    CaseFilterDto<CaseQuery> saveIssueFilter( CaseFilterDto<CaseQuery> filter ) throws RequestFailedException;

    Long removeIssueFilter(Long id ) throws RequestFailedException;
}
