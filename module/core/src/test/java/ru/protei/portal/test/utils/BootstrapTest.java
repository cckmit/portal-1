package ru.protei.portal.test.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.bootstrap.BootstrapServiceImpl;

import java.util.*;

import static ru.protei.portal.core.model.dict.En_HistoryAction.*;
import static ru.protei.portal.core.model.dict.En_HistoryType.CASE_IMPORTANCE;
import static ru.protei.portal.core.model.dict.En_HistoryType.CASE_MANAGER;
import static ru.protei.portal.core.model.query.CaseCommentQuery.CommentType.*;
import static ru.protei.portal.core.model.util.CrmConstants.ImportanceLevel.*;
import static ru.protei.portal.core.model.util.CrmConstants.State.*;

public class BootstrapTest {
    @Test
    public void stateImportanceManagerCommentToHistoryMigrationTest() {
        long id = 1;
        List<CaseComment> caseComments = new ArrayList<>();
        caseComments.add(fillStateComment(createComment(id++, 1L), CREATED, "created"));
        caseComments.add(fillImportanceComment(createComment(id++, 1L), BASIC));


        caseComments.add(fillImportanceComment(createComment(id++, 2L), BASIC));
        caseComments.add(fillStateComment(createComment(id++, 2L), CREATED, "created"));
        caseComments.add(fillManagerComment(createComment(id++, 2L), 1L, "manager1"));


        caseComments.add(fillImportanceComment(createComment(id++, 1L), COSMETIC));
        caseComments.add(fillManagerComment(createComment(id++, 1L), 2L, "manager2"));

        caseComments.add(fillManagerComment(createComment(id++, 1L), null, null));

        caseComments.add(fillImportanceComment(createComment(id++, 1L), CRITICAL));
        caseComments.add(fillStateComment(createComment(id++, 1L), ACTIVE, "active"));
        caseComments.add(fillManagerComment(createComment(id++, 1L), 1L, "manager1"));

        caseComments.add(fillStateComment(createComment(id++, 1L), DONE, "done"));

        BootstrapServiceImpl bootstrapService = new BootstrapServiceImpl();
        Map<CaseCommentQuery.CommentType, BootstrapServiceImpl.CommentToHistoryMigration> commentToHistoryMigrationMap = new HashMap<>();

        commentToHistoryMigrationMap.put(CASE_STATE, new BootstrapServiceImpl.CommentToHistoryMigration(CASE_STATE, En_HistoryType.CASE_STATE,
                CaseComment::getCaseStateId, CaseComment::getCaseStateName));
        commentToHistoryMigrationMap.put(IMPORTANCE, new BootstrapServiceImpl.CommentToHistoryMigration(IMPORTANCE, CASE_IMPORTANCE,
                caseComment -> (long) caseComment.getCaseImpLevel(),
                CaseComment::getImportanceCode));
        commentToHistoryMigrationMap.put(MANAGER, new BootstrapServiceImpl.CommentToHistoryMigration(MANAGER, CASE_MANAGER,
                CaseComment::getCaseManagerId, CaseComment::getCaseManagerShortName));

        final List<History> histories = bootstrapService.convertCaseCommentToHistory(caseComments, commentToHistoryMigrationMap);

        Assert.assertNotNull(histories);
        Assert.assertEquals(12, histories.size());

        Assert.assertTrue(check(histories.get(0), 1, En_HistoryType.CASE_STATE, ADD, null, 1L));
        Assert.assertTrue(check(histories.get(1), 1, En_HistoryType.CASE_IMPORTANCE, ADD, null, 3L));
        Assert.assertTrue(check(histories.get(2), 1, En_HistoryType.CASE_IMPORTANCE, CHANGE, 3L, 4L));
        Assert.assertTrue(check(histories.get(3), 1, CASE_MANAGER, ADD, null, 2L));
        Assert.assertTrue(check(histories.get(4), 1, CASE_MANAGER, REMOVE, 2L, null));
        Assert.assertTrue(check(histories.get(5), 1, CASE_IMPORTANCE, CHANGE, 4L, 1L));
        Assert.assertTrue(check(histories.get(6), 1, En_HistoryType.CASE_STATE, CHANGE, 1L, 16L));
        Assert.assertTrue(check(histories.get(7), 1, CASE_MANAGER, ADD, null, 1L));
        Assert.assertTrue(check(histories.get(8), 1, En_HistoryType.CASE_STATE, CHANGE, 16L, 17L));

        Assert.assertTrue(check(histories.get(9), 2, CASE_IMPORTANCE, ADD, null, 3L));
        Assert.assertTrue(check(histories.get(10), 2, En_HistoryType.CASE_STATE, ADD, null, 1L));
        Assert.assertTrue(check(histories.get(11), 2, CASE_MANAGER, ADD, null, 1L));
    }

    private boolean check(History h, long caseId, En_HistoryType enHistoryType, En_HistoryAction enHistoryAction, Long oldId, Long newId) {
        return Objects.equals(h.getCaseObjectId(), caseId) &&
                h.getType() == enHistoryType &&
                h.getAction() == enHistoryAction &&
                Objects.equals(h.getOldId(), oldId) &&
                Objects.equals(h.getNewId(), newId);
    }

    private CaseComment fillStateComment(CaseComment comment, long stateId, String stateName) {
        comment.setCaseStateId(stateId);
        comment.setCaseStateName(stateName);
        return comment;
    }

    private CaseComment fillImportanceComment(CaseComment comment, Integer importanceLevelId) {
        comment.setCaseImpLevel(importanceLevelId);
        return comment;
    }

    private CaseComment fillManagerComment(CaseComment comment, Long managerId, String managerName) {
        comment.setCaseManagerId(managerId);
        comment.setCaseManagerShortName(managerName);
        return comment;
    }

    private CaseComment createComment(long id, long caseId) {
        CaseComment comment = new CaseComment();
        comment.setId(id);
        comment.setCreated(now);
        comment.setCaseId(caseId);
        return comment;
    }

    private Date now = new Date();
}
