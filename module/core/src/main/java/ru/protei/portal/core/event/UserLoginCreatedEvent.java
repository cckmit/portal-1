package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.UserLoginPassword;

public class UserLoginCreatedEvent extends ApplicationEvent {

    private UserLoginPassword userLoginPassword;
    private NotificationEntry notificationEntry;

    public UserLoginCreatedEvent(UserLoginPassword userLoginPassword) {
        this(userLoginPassword, null);
    }

    public UserLoginCreatedEvent(UserLoginPassword userLoginPassword, NotificationEntry notificationEntry) {
        super(userLoginPassword);
        this.userLoginPassword = userLoginPassword;
        this.notificationEntry = notificationEntry;
    }

    public UserLoginPassword getUserLoginPassword() {
        return userLoginPassword;
    }

    public NotificationEntry getNotificationEntry() {
        return notificationEntry;
    }
}
