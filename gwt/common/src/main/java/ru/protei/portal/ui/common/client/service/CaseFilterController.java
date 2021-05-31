package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления фильтрами обращений
 */
@RemoteServiceRelativePath( "springGwtServices/CaseFilterController" )
public interface CaseFilterController extends RemoteService {

    /**
     * Получение списка сокращенного представления CaseFilter
     */
    List<FilterShortView> getCaseFilterShortViewList(En_CaseFilterType filterType) throws RequestFailedException;

    CaseFilterDto<HasFilterQueryIds> getCaseFilter(Long id) throws RequestFailedException;

    SelectorsParams getSelectorsParams(HasFilterQueryIds filterEntityIds) throws RequestFailedException;

    CaseFilterDto<ProjectQuery> saveProjectFilter(CaseFilterDto<ProjectQuery> caseFilterDto) throws RequestFailedException;

    CaseFilterDto<DeliveryQuery> saveDeliveryFilter(CaseFilterDto<DeliveryQuery> caseFilterDto) throws RequestFailedException;

    CaseFilterDto<CaseQuery> saveIssueFilter(CaseFilterDto<CaseQuery> caseFilterDto) throws RequestFailedException;

    Long removeCaseFilter(Long id) throws RequestFailedException;
}
