package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.core.model.ent.*;

import java.util.List;

public interface CommonService {
    CaseComment parseJournalToCaseComment(Journal journal, long companyId);

    CaseComment parseJournalToStatusComment(Journal journal, long companyId, long statusMapId);

    List<Attachment> processAttachments( Issue issue, CaseObject obj, Long contactPersonId, RedmineEndpoint endpoint);

    void processUpdateCreationDateAttachments(Issue issue, Long caseObjId);

    CaseComment processStoreComment(Issue issue, Long contactPersonId, CaseObject obj, Long caseObjId, CaseComment comment);

    Person getAssignedPerson(Long companyId, User user);
}
