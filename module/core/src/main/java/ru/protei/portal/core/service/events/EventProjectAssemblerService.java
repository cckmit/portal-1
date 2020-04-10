package ru.protei.portal.core.service.events;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.ProjectCommentEvent;
import ru.protei.portal.core.event.ProjectLinkEvent;
import ru.protei.portal.core.event.ProjectSaveEvent;

public interface EventProjectAssemblerService {
    @EventListener
    void onProjectSaveEvent(ProjectSaveEvent event);

    @EventListener
    void onProjectCommentEvent(ProjectCommentEvent event);

    @EventListener
    void onProjectLinkEvent(ProjectLinkEvent event);
}
