package ru.protei.portal.redmine.factory;

import com.taskadapter.redmineapi.bean.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseNameAndDescriptionEvent;
import ru.protei.portal.core.event.CaseObjectMetaEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.RedminePriorityMapEntryDAO;
import ru.protei.portal.core.model.dao.RedmineToCrmStatusMapEntryDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.DiffResult;
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

    public FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> getCommentsUpdater() {
        return funcs.get(COMMENT);
    }

    private class CaseStatusUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {

            Integer newStatus = parseToInteger(value);
            logger.debug("Trying to get portal status id matching with redmine {}", newStatus);
            final RedmineToCrmEntry redmineStatusEntry = statusMapEntryDAO.getLocalStatus(endpoint.getStatusMapId(), newStatus);

            if (redmineStatusEntry != null) {

                final CaseObjectMeta oldMeta = new CaseObjectMeta(object);
                final Person author = personMapper.toProteiPerson(journal.getUser());

                object.setStateId(redmineStatusEntry.getLocalStatusId());
                caseObjectDAO.merge(object);
                logger.debug("Updated case state, old={}, new={}", En_CaseState.getById(oldMeta.getStateId()), En_CaseState.getById(object.getStateId()));

                Long stateCommentId = commonService.createAndStoreStateComment(journal.getCreatedOn(), author.getId(), redmineStatusEntry.getLocalStatusId().longValue(), object.getId());
                if (stateCommentId == null) {
                    logger.error("State comment for the issue {} not saved!", object.getId());
                }

                publisherService.publishEvent(new CaseObjectMetaEvent(
                        this,
                        ServiceModule.REDMINE,
                        author.getId(),
                        En_ExtAppType.forCode(object.getExtAppType()),
                        oldMeta,
                        new CaseObjectMeta(object)));
            } else {
                logger.warn("Status was not found");
            }
        }
    }

    private class CasePriorityUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {

            Integer newPriority = parseToInteger(value);
            logger.debug("Trying to get portal priority level id matching with redmine {}", value);
            final RedminePriorityMapEntry priorityMapEntry = priorityMapEntryDAO.getByRedminePriorityId(newPriority, endpoint.getPriorityMapId());

            if (priorityMapEntry != null) {

                final CaseObjectMeta oldMeta = new CaseObjectMeta(object);
                final Person author = personMapper.toProteiPerson(journal.getUser());

                object.setImpLevel(priorityMapEntry.getLocalPriorityId());
                caseObjectDAO.merge(object);
                logger.debug("Updated case priority, old={}, new={}", En_ImportanceLevel.find(oldMeta.getImpLevel()), En_ImportanceLevel.find(object.getImpLevel()));

                Long ImportanceCommentId = commonService.createAndStoreImportanceComment(journal.getCreatedOn(), author.getId(), priorityMapEntry.getLocalPriorityId(), object.getId());
                if (ImportanceCommentId == null) {
                    logger.error("Importance comment for the issue {} not saved!", object.getId());
                }

                publisherService.publishEvent(new CaseObjectMetaEvent(
                        this,
                        ServiceModule.REDMINE,
                        author.getId(),
                        En_ExtAppType.forCode(object.getExtAppType()),
                        oldMeta,
                        new CaseObjectMeta(object)));
            } else {
                logger.warn("Priority was not found");
            }
        }
    }

    private class CaseDescriptionUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {

            final DiffResult<String> infoDiff = new DiffResult<>(object.getInfo(), value);
            final Person author = personMapper.toProteiPerson(journal.getUser());

            object.setInfo(value);
            caseObjectDAO.merge(object);
            logger.debug("Updated case info, old={}, new={}", infoDiff.getInitialState(), infoDiff.getNewState());

            publisherService.publishEvent(new CaseNameAndDescriptionEvent(
                    this,
                    object.getId(),
                    new DiffResult<>(null, object.getName()),
                    infoDiff,
                    author.getId(),
                    ServiceModule.REDMINE,
                    En_ExtAppType.forCode(object.getExtAppType())));
        }
    }

    private class CaseSubjectUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {

            final DiffResult<String> nameDiff = new DiffResult<>(object.getName(), value);
            final Person author = personMapper.toProteiPerson(journal.getUser());

            object.setName(value);
            caseObjectDAO.merge(object);
            logger.debug("Updated case name, old={}, new={}", nameDiff.getInitialState(), nameDiff.getNewState());

            publisherService.publishEvent(new CaseNameAndDescriptionEvent(
                    this,
                    object.getId(),
                    nameDiff,
                    new DiffResult<>(null, object.getInfo()),
                    author.getId(),
                    ServiceModule.REDMINE,
                    En_ExtAppType.forCode(object.getExtAppType())));
        }
    }

    private class CaseCommentUpdater implements FourConsumer<CaseObject, RedmineEndpoint, Journal, String, CachedPersonMapper> {
        @Override
        public void apply(CaseObject object, RedmineEndpoint endpoint, Journal journal, String value, CachedPersonMapper personMapper) {
            CaseComment caseComment = commonService.parseJournalToCaseComment(journal, personMapper.toProteiPerson(journal.getUser()));
            commonService.processStoreComment(caseComment.getAuthor().getId(), object.getId(), caseComment);

            logger.debug("Added {} new case comment to case with id {}", caseComment.getId(), object.getId());
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

    private Integer parseToInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Can't parse value {} to Integer", value);
            return null;
        }
    }

    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineToCrmStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CommonService commonService;

    @Autowired
    private EventPublisherService publisherService;

    private final static Logger logger = LoggerFactory.getLogger(CaseUpdaterFactory.class);
}
