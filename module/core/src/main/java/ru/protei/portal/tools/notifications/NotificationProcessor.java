package ru.protei.portal.tools.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.CaseSubscriptionService;
import ru.protei.portal.core.service.TemplateService;
import ru.protei.portal.core.service.template.PreparedTemplate;

import java.util.List;
import java.util.Set;

/**
 * Created by shagaleev on 30/05/17.
 */
public class NotificationProcessor {

    private final static Logger log = LoggerFactory.getLogger( NotificationProcessor.class );

    @Autowired
    CaseSubscriptionService subscriptionService;

    @Autowired
    TemplateService templateService;

    @Autowired
    CaseService caseService;

    @EventListener
    public void onCaseChanged( CaseObjectEvent event ) {
        Set<NotificationEntry> notificationEntries = subscriptionService.subscribers( event );
        if ( notificationEntries.isEmpty() ) {
            return;
        }

        CoreResponse<List<CaseComment> > comments = caseService.getCaseCommentList( event.getCaseObject().getId() );
        if ( comments.isError() ) {
            log.error( "Failed to retrieve comments for caseId={}", event.getCaseObject().getId() );
            return;
        }

        PreparedTemplate template = templateService.getCrmEmailNotificationBody( event, comments.getData() );
        if ( template == null ) {
            log.error( "Failed to prepare template for event {}", event );
            return;
        }

        notificationEntries.stream().forEach( (entry)->{
            String messageBody = template.getText( entry.getAddress(), entry.getLangCode() );
            log.info( "Email Notification send to {} subject={} body = {}", entry.getAddress(), "", messageBody );
        } );
    }
}
