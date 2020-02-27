package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseNameAndDescriptionEvent;
import ru.protei.portal.core.event.CaseObjectMetaEvent;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.redmine.factory.CaseUpdaterFactory;
import ru.protei.portal.redmine.utils.CachedPersonMapper;

import java.util.Collection;
import java.util.Date;

public interface CommonService {

    void processAttachments(Issue issue, CachedPersonMapper personMapper, CaseObject obj, RedmineEndpoint endpoint);

    void processComments(Collection<Journal> journals, CachedPersonMapper personMapper, CaseObject obj);

    CaseComment createAndStoreComment(Date creationDate, String text, Person author, Long caseId);

    Long createAndStoreStateComment(Date created, Long authorId, Long stateId, Long caseObjectId);

    Long createAndStoreImportanceComment(Date created, Long authorId, Integer importance, Long caseId);

    void updateCaseStatus( CaseObject object, Long statusMapId, Date creationOn, String value, Person author );

    void updateCasePriority( CaseObject object, Long priorityMapId, Journal journal, String value, Person author );

    void updateCaseDescription( CaseObject object, String value, Person author );

    void updateCaseSubject( CaseObject object, String value, Person author );

    void updateComment( Long objectId, Date creationDate, String text, Person author );
}
