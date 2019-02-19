package ru.protei.portal.jira.handlers;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.collect.MapBuilder;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component
public class JiraEventHandlerImpl {
    private static final Logger log = LoggerFactory.getLogger(JiraEventHandlerImpl.class);


    @Autowired
    public JiraEventHandlerImpl(EventPublisher eventPublisher) {
        eventPublisher.register(this);    // Demonstration only -- don't do this in real code!
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        issueEvent.getChangeLog();
        Long eventTypeId = issueEvent.getEventTypeId();
        com.atlassian.jira.issue.Issue issue = issueEvent.getIssue();
        ApplicationUser user = issueEvent.getUser();


        // if it's an event we're interested in, log it
        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
            log.info("Issue {} has been created at {}.", issue.getKey(), issue.getCreated());
        } else if (eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)) {
            log.info("Issue {} has been resolved at {}.", issue.getKey(), issue.getResolutionDate());
        } else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
            log.info("Issue {} has been closed at {}.", issue.getKey(), issue.getUpdated());
        } else if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
            List<GenericValue> changeItems = null;

            try {
                GenericValue changeLog = issueEvent.getChangeLog();
                changeItems = changeLog.internalDelegator.findByAnd("ChangeItem", MapBuilder.build("group",changeLog.get("id")));
            } catch (GenericEntityException e){
                System.out.println(e.getMessage());
            }

            log.info("number of changes: {}",changeItems.size());
            for (Iterator<GenericValue> iterator = changeItems.iterator(); iterator.hasNext();){
                GenericValue changetemp = (GenericValue) iterator.next();
                String field = changetemp.getString("field");
                String oldstring = changetemp.getString("oldstring");
                String newstring = changetemp.getString("newstring");
                StringBuilder fullstring = new StringBuilder();
                fullstring.append("Issue ");
                fullstring.append(issue.getKey());
                fullstring.append(" field ");
                fullstring.append(field);
                fullstring.append(" has been updated from ");
                fullstring.append(oldstring);
                fullstring.append(" to ");
                fullstring.append(newstring);
                log.info("changes {}", fullstring.toString());

                if(field == "Component") changeAssignee(changetemp, issue, user);
            }
        }
    }


    private void changeAssignee(GenericValue changetemp, Issue issue, ApplicationUser user) {
        String currentAssignee = issue.getAssigneeId();
        String componentName = null;
        String componentLead = null;
        Collection<ProjectComponent> components = issue.getComponentObjects();
        //log.info("current assignee: {}", currentAssignee);

        for (Iterator<ProjectComponent> iterator = components.iterator(); iterator.hasNext();){
            ProjectComponent component = (ProjectComponent) iterator.next();
            componentName = component.getName();
            componentLead = component.getLead();
            log.info("component name: {}", componentName);
            log.info("component lead: {}", componentLead);

        }
        if (currentAssignee != componentLead && components.size() == 1){
            MutableIssue mIssue = (MutableIssue) issue;
            mIssue.setAssigneeId(componentLead);
            IssueService issueService = ComponentAccessor.getIssueService();

            IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
            issueInputParameters.setAssigneeId(componentLead);

            IssueService.UpdateValidationResult updateValidationResult = issueService.validateUpdate(user,mIssue.getId(), issueInputParameters);

            if (updateValidationResult.isValid())
            {
                IssueService.IssueResult updateResult = issueService.update(user, updateValidationResult);
                if (!updateResult.isValid())
                {
                    log.info("Issue Assignee changed");
                }
            }
        }
    }
}
