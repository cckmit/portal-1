package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;

public interface CommonService {
    CaseComment parseJournal(Journal journal, long companyId);

    void processAttachments(Issue issue, CaseObject obj, Person contactPerson);

    CaseComment processStoreComment(Issue issue, Person contactPerson, CaseObject obj, Long caseObjId, CaseComment comment);

    Person getAssignedPerson(Long companyId, User user);
}