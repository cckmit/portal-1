package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления контактами
 */
@RemoteServiceRelativePath( "springGwtServices/IssueService" )
public interface IssueService extends RemoteService {

    List< CaseObject > getIssues( CaseQuery query ) throws RequestFailedException;

    CaseObject getIssue( long id ) throws RequestFailedException;

    Boolean saveIssue( CaseObject p ) throws RequestFailedException;

    /**
     * Получение списка статусов
     * @return список статусов
     */
    List<En_CaseState> getStateList() throws RequestFailedException;

    long getIssuesCount( CaseQuery query ) throws RequestFailedException;

    /**
     * Получение списка комментариев по обращению
     * @param caseId
     */
    List<CaseComment> getIssueComments( Long caseId ) throws RequestFailedException;

    /**
     * Удаление комментария обращения
     *
     * @param value
     */
    void removeIssueComment( CaseComment value ) throws RequestFailedException;

    /**
     * Редактирование комментария обращения
     *
     * @param value
     */
    CaseComment editIssueComment( CaseComment value ) throws RequestFailedException;
}
