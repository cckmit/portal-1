package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.view.EmployeeShortView;

import java.util.Date;
import java.util.List;

public class BirthdaysNotificationEvent extends ApplicationEvent {

    private final List<EmployeeShortView> employees;
    private final Date from;
    private final Date to;
    private final List<NotificationEntry> notifiers;

    public BirthdaysNotificationEvent(Object source, List<EmployeeShortView> employees,
                                      Date from, Date to,
                                      List<NotificationEntry> notifiers) {
        super(source);
        this.employees = employees;
        this.from = from;
        this.to = to;
        this.notifiers = notifiers;
    }

    public List<EmployeeShortView> getEmployees() { return employees; }

    public Date getFromDate () { return from; }

    public Date getToDate () { return to; }

    public List<NotificationEntry> getNotifiers() { return notifiers; }
}
