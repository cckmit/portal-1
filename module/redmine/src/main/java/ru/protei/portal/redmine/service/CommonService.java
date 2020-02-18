package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.utils.CachedPersonMapper;

import java.util.Collection;
import java.util.Date;

public interface CommonService {
    CaseComment parseJournalToCaseComment(Journal journal, Person person);

    void processAttachments(Issue issue, CachedPersonMapper personMapper, CaseObject obj, RedmineEndpoint endpoint);

    void processComments(Collection<Journal> journals, CachedPersonMapper personMapper, CaseObject obj);

    CaseComment processStoreComment(Long authorId, Long caseObjectId, CaseComment comment);

    Long createAndStoreStateComment(Date created, Long authorId, Long stateId, Long caseObjectId);

    Long createAndStoreImportanceComment(Date created, Long authorId, Integer importance, Long caseId);

    boolean isTechUser(RedmineEndpoint endpoint, User user);
}
