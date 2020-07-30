package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

public interface CaseCommentControllerAsync {

    void getCaseComments(En_CaseType caseType, Long caseId, AsyncCallback<List<CaseComment>> async);

    void saveCaseComment(En_CaseType caseType, CaseComment comment, AsyncCallback<CaseComment> async);

    void removeCaseComment(En_CaseType caseType, CaseComment comment, AsyncCallback<Void> async);

    void updateCaseTimeElapsedType(Long caseCommentId, En_TimeElapsedType type, AsyncCallback<Boolean> async);

    void getCaseComment( Long commentId, AsyncCallback<CaseComment> async );
}
