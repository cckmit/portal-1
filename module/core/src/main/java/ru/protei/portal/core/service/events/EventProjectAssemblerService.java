package ru.protei.portal.core.service.events;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.ProjectCommentEvent;
import ru.protei.portal.core.event.ProjectCreateEvent;
import ru.protei.portal.core.event.ProjectLinkEvent;
import ru.protei.portal.core.event.ProjectUpdateEvent;

public interface EventProjectAssemblerService {
    @EventListener
    void onProjectCreateEvent(ProjectCreateEvent event);

    @EventListener
    void onProjectUpdateEvent(ProjectUpdateEvent event);

    @EventListener
    void onProjectCommentEvent(ProjectCommentEvent event);

    @EventListener
    void onProjectLinkEvent(ProjectLinkEvent event);
}
