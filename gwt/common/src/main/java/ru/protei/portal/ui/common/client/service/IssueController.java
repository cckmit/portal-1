package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления контактами
 */
@RemoteServiceRelativePath( "springGwtServices/IssueController" )
public interface IssueController extends RemoteService {

    List<CaseShortView> getIssues( CaseQuery query ) throws RequestFailedException;

    CaseObject getIssue( long id ) throws RequestFailedException;

    CaseObject saveIssue( CaseObject p ) throws RequestFailedException;

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
     * Удаляет все вложения из БД и Cloud которые привязаны к комментарию
     */
    void removeIssueComment( CaseComment value ) throws RequestFailedException;

    /**
     * Редактирование комментария обращения
     * Удаляет все вложения из БД и Cloud которые ранее были привязаны к комментарию
     */
    CaseComment editIssueComment( CaseComment comment ) throws RequestFailedException;

    CaseInfo getIssueShortInfo(Long caseNumber) throws RequestFailedException;
}
