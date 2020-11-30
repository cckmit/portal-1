package ru.protei.portal.core.service.events;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.*;

public interface EventProjectAssemblerService {
    @EventListener
    void onProjectCreateEvent(ProjectCreateEvent event);

    @EventListener
    void onProjectUpdateEvent(ProjectUpdateEvent event);

    @EventListener
    void onProjectCommentEvent(ProjectCommentEvent event);

    @EventListener
    void onProjectLinkEvent(ProjectLinkEvent event);

    @EventListener
    void onProjectAttachmentEvent(ProjectAttachmentEvent event);
}
