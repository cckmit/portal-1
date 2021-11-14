package ru.protei.portal.core.service.events;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.EmployeeRegistrationCommentEvent;

public interface EventEmployeeRegistrationAssemblerService {

    @EventListener
    void onEmployeeRegistrationCommentEvent(EmployeeRegistrationCommentEvent event);
}
