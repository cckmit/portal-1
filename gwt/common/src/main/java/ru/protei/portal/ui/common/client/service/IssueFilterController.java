package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.HasFilterEntityIds;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;
import ru.protei.portal.core.model.view.filterwidget.DtoFilterQuery;
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
    List<AbstractFilterShortView> getIssueFilterShortViewList(En_CaseFilterType filterType ) throws RequestFailedException;

    <T extends DtoFilterQuery> CaseFilterDto<T> getIssueFilter(Long id ) throws RequestFailedException;

    SelectorsParams getSelectorsParams( HasFilterEntityIds filterEntityIds ) throws RequestFailedException;

    <T extends DtoFilterQuery> CaseFilterDto<T> saveIssueFilter( CaseFilterDto<T> filter ) throws RequestFailedException;

    Long removeIssueFilter(Long id ) throws RequestFailedException;
}
