package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.core.model.ent.*;

import java.util.Date;

public interface CommonService {
    CaseComment parseJournalToCaseComment(Journal journal, long companyId);

    void processAttachments( Issue issue, CaseObject obj, RedmineEndpoint endpoint);

    void processUpdateCreationDateAttachments(Issue issue, Long caseObjId);

    CaseComment processStoreComment(Long authorId, Long caseObjectId, CaseComment comment);

    void createAndStoreStateComment(Date created, Long authorId, Long stateId, Long caseObjectId);

    Person getAssignedPerson(Long companyId, User user);
}
