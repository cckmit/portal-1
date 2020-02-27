package ru.protei.portal.redmine.factory;

import com.taskadapter.redmineapi.bean.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.RedminePriorityMapEntryDAO;
import ru.protei.portal.core.model.dao.RedmineToCrmStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.redmine.enums.RedmineChangeType;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.CachedPersonMapper;
import ru.protei.portal.redmine.utils.FourConsumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ru.protei.portal.redmine.enums.RedmineChangeType.*;

public class CaseUpdaterFactory {

    public FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> getUpdater(RedmineChangeType type) {
        return funcs.get(type);
    }

    private class CaseStatusUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {
            commonService.updateCaseStatus( object,  endpoint.getStatusMapId(), journal.getCreatedOn(),  value,  personMapper.toProteiPerson(journal.getUser()) );
        }
    }

    private class CasePriorityUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {
            commonService.updateCasePriority( object, endpoint.getPriorityMapId(), journal, value, personMapper.toProteiPerson(journal.getUser()) );
        }
    }

    private class CaseDescriptionUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {
            commonService.updateCaseDescription( object, value, personMapper.toProteiPerson(journal.getUser()) );
        }
    }

    private class CaseSubjectUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {
            commonService.updateCaseSubject(  object,  value,  personMapper.toProteiPerson(journal.getUser()) );
        }
    }

    private class CaseCommentUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {
            commonService.updateComment(  object.getId(), journal.getCreatedOn(), journal.getNotes(),  personMapper.toProteiPerson(journal.getUser()) );
        }
    }

    private final ConcurrentMap<RedmineChangeType, FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> > funcs =
            new ConcurrentHashMap<RedmineChangeType, FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> >() {{
                put(STATUS_CHANGE, new CaseStatusUpdater());
                put(PRIORITY_CHANGE, new CasePriorityUpdater());
                put(SUBJECT_CHANGE, new CaseSubjectUpdater());
                put(DESCRIPTION_CHANGE, new CaseDescriptionUpdater());
                put(COMMENT, new CaseCommentUpdater());
            }};



    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineToCrmStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    public CommonService commonService;

    @Autowired
    private EventPublisherService publisherService;

    private final static Logger logger = LoggerFactory.getLogger(CaseUpdaterFactory.class);
}
