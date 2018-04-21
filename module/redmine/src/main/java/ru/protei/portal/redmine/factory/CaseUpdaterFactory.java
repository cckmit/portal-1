package ru.protei.portal.redmine.factory;

import com.taskadapter.redmineapi.bean.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.RedminePriorityMapEntryDAO;
import ru.protei.portal.core.model.dao.RedmineToCrmStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.core.model.ent.RedminePriorityMapEntry;
import ru.protei.portal.core.model.ent.RedmineToCrmEntry;
import ru.protei.portal.redmine.enums.RedmineChangeType;
import ru.protei.portal.redmine.utils.RedmineUtils;
import ru.protei.portal.redmine.utils.TriConsumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ru.protei.portal.redmine.enums.RedmineChangeType.*;

public class CaseUpdaterFactory {

    public TriConsumer<CaseObject, Issue, RedmineEndpoint> getUpdater(RedmineChangeType type) {
        return funcs.get(type);
    }

    private class CaseStatusUpdater implements TriConsumer<CaseObject, Issue, RedmineEndpoint> {
        @Override
        public void apply(CaseObject object, Issue issue, RedmineEndpoint endpoint) {
            final long statusMapId = endpoint.getStatusMapId();

            logger.debug("Trying to get portal status id matching with redmine: {}", issue.getStatusId());

            final RedmineToCrmEntry redmineStatusEntry =
                    statusMapEntryDAO.getLocalStatus(statusMapId, issue.getStatusId());

            if (redmineStatusEntry != null && !redmineStatusEntry.getLocalStatusId().equals(object.getStateId())) {
                logger.debug("Found status id: {}", redmineStatusEntry.getLocalStatusId());
                object.setStateId(redmineStatusEntry.getLocalStatusId());
            } else
                logger.debug("Status was not found");
        }
    }

    private class CasePriorityUpdater implements TriConsumer<CaseObject, Issue, RedmineEndpoint> {
        @Override
        public void apply(CaseObject object, Issue issue, RedmineEndpoint endpoint) {
            final long priorityMapId = endpoint.getPriorityMapId();

            logger.debug("Trying to get portal priority level id matching with redmine: {}",
                    issue.getCustomFieldById(RedmineUtils.REDMINE_CUSTOM_FIELD_ID).getValue());
            final String redminePriorityName = issue.getCustomFieldById(RedmineUtils.REDMINE_CUSTOM_FIELD_ID).getValue();

            final RedminePriorityMapEntry priorityMapEntry =
                    priorityMapEntryDAO.getByRedminePriorityName(redminePriorityName, priorityMapId);

            if (priorityMapEntry != null) {
                logger.debug("Found priority level id: {}", priorityMapEntry.getLocalPriorityId());
                object.setImpLevel(priorityMapEntry.getLocalPriorityId());
            } else
                logger.debug("Status was not found");
        }
    }

    private class CaseDescriprionUpdater implements TriConsumer<CaseObject, Issue, RedmineEndpoint> {
        @Override
        public void apply(CaseObject object, Issue issue, RedmineEndpoint endpoint) {
            if (!object.getInfo().equals(issue.getDescription()))
                object.setInfo(issue.getDescription());
        }
    }

    private class CaseSubjectUpdater implements TriConsumer<CaseObject, Issue, RedmineEndpoint> {
        @Override
        public void apply(CaseObject object, Issue issue, RedmineEndpoint endpoint) {
            if (!object.getName().equals(issue.getSubject()))
                object.setName(issue.getSubject());
        }
    }

    private final ConcurrentMap<RedmineChangeType, TriConsumer<CaseObject, Issue, RedmineEndpoint>> funcs =
            new ConcurrentHashMap<RedmineChangeType, TriConsumer<CaseObject, Issue, RedmineEndpoint>>() {{
                put(STATUS_CHANGE, new CaseStatusUpdater());
                put(PRIORITY_CHANGE, new CasePriorityUpdater());
                put(SUBJECT_CHANGE, new CaseSubjectUpdater());
                put(DESCRIPTION_CHANGE, new CaseDescriprionUpdater());
            }};

    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineToCrmStatusMapEntryDAO statusMapEntryDAO;

    private final static Logger logger = LoggerFactory.getLogger(CaseUpdaterFactory.class);
}
