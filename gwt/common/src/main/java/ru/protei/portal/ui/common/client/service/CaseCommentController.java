package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/CaseCommentController")
public interface CaseCommentController extends RemoteService {

    /**
     * Получение списка комментариев
     */
    List<CaseComment> getCaseComments(En_CaseType caseType, Long caseId) throws RequestFailedException;

    /**
     * Редактирование комментария
     * Удаляет все вложения из БД и Cloud которые ранее были привязаны к комментарию
     */
    CaseComment saveCaseComment(En_CaseType caseType, CaseComment comment) throws RequestFailedException;

    /**
     * Удаление комментария
     * Удаляет все вложения из БД и Cloud которые привязаны к комментарию
     */
    void removeCaseComment(En_CaseType caseType, CaseComment comment) throws RequestFailedException;
}