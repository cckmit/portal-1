package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.ent.SelectorsParamsRequest;
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

    CaseFilter getIssueFilter(Long id ) throws RequestFailedException;

    SelectorsParams getSelectorsParams(SelectorsParamsRequest selectorsParamsRequest) throws RequestFailedException;

    CaseFilter saveIssueFilter( CaseFilter filter ) throws RequestFailedException;

    boolean removeIssueFilter( Long id ) throws RequestFailedException;
}
