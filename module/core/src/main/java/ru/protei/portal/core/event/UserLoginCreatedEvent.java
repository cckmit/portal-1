package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.struct.NotificationEntry;

public class UserLoginCreatedEvent extends ApplicationEvent {

    private String login;
    private String passwordRaw;
    private String displayName;
    private NotificationEntry notificationEntry;

    public UserLoginCreatedEvent(String login, String passwordRaw, String displayName, NotificationEntry notificationEntry) {
        super(login);
        this.login = login;
        this.passwordRaw = passwordRaw;
        this.displayName = displayName;
        this.notificationEntry = notificationEntry;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordRaw() {
        return passwordRaw;
    }

    public String getDisplayName() {
        return displayName;
    }

    public NotificationEntry getNotificationEntry() {
        return notificationEntry;
    }
}
