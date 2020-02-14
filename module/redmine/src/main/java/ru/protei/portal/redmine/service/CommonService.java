package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.redmine.utils.CachedPersonMapper;

import java.util.Date;

public interface CommonService {
    CaseComment parseJournalToCaseComment(Journal journal, Person person);

    void processAttachments(Issue issue, CachedPersonMapper personMapper, CaseObject obj, RedmineEndpoint endpoint);

    void processUpdateCreationDateAttachments(Issue issue, Long caseObjId);

    CaseComment processStoreComment(Long authorId, Long caseObjectId, CaseComment comment);

    Long createAndStoreStateComment(Date created, Long authorId, Long stateId, Long caseObjectId);

    Long createAndStoreImportanceComment(Date created, Long authorId, Integer importance, Long caseId);

    Person getAssignedPerson(Long companyId, User user);
}
