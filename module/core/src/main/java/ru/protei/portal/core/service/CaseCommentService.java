package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseCommentQuery;

import java.util.List;

/**
 * Service to get/add/update/remove comments {@link ru.protei.portal.core.model.ent.CaseComment}
 * of types {@link ru.protei.portal.core.model.dict.En_CaseType}
 * Privileges are checked manually
 * Audit performed manually
 */
public interface CaseCommentService {

    CoreResponse<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, long caseObjectId);

    CoreResponse<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, CaseCommentQuery query);

    CoreResponse<CaseComment> addCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Person person);

    CoreResponse<CaseComment> updateCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Person person);

    CoreResponse<Boolean> removeCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Long personId);

    CoreResponse<Long> getTimeElapsed(Long caseId);

    CoreResponse<Boolean> updateTimeElapsed(AuthToken token, Long caseId);

    CoreResponse<Boolean> updateCaseTimeElapsed(AuthToken token, Long caseId, long timeElapsed);

    CoreResponse<Long> addCommentOnSentReminder( CaseComment comment );
}
