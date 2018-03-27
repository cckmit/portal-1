package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.IssueFilter;
import ru.protei.portal.core.model.view.IssueFilterShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления фильтрами обращений
 */
@RemoteServiceRelativePath( "springGwtServices/IssueFilterService" )
public interface IssueFilterService extends RemoteService {

    /**
     * Получение списка сокращенного представления IssueFilter
     */
    List< IssueFilterShortView > getIssueFilterShortViewListByCurrentUser() throws RequestFailedException;

    IssueFilter getIssueFilter( Long id ) throws RequestFailedException;

    IssueFilter saveIssueFilter( IssueFilter filter ) throws RequestFailedException;

    boolean removeIssueFilter( Long id ) throws RequestFailedException;
}
