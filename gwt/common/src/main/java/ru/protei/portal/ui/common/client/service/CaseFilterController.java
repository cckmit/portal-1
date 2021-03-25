package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;
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
    List<AbstractFilterShortView> getCaseFilterShortViewList(En_CaseFilterType filterType) throws RequestFailedException;

    <T extends HasFilterQueryIds> CaseFilterDto<T> getCaseFilter(Long id) throws RequestFailedException;

    SelectorsParams getSelectorsParams(HasFilterQueryIds filterEntityIds) throws RequestFailedException;

    <T extends HasFilterQueryIds> CaseFilterDto<T> saveCaseFilter(CaseFilterDto<T> filter) throws RequestFailedException;

    Long removeCaseFilter(Long id) throws RequestFailedException;
}
