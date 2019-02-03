package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.struct.NotificationEntry;

public class UserLoginUpdateEvent extends ApplicationEvent {

    private boolean isNewAccount = false;
    private String login;
    private String passwordRaw;
    private String displayName;
    private NotificationEntry notificationEntry;

    public UserLoginUpdateEvent(String login, String passwordRaw, String displayName, boolean isNewAccount, NotificationEntry notificationEntry) {
        super(login);
        this.login = login;
        this.passwordRaw = passwordRaw;
        this.displayName = displayName;
        this.notificationEntry = notificationEntry;
        this.isNewAccount = isNewAccount;
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

    public boolean isNewAccount() {
        return isNewAccount;
    }

    public NotificationEntry getNotificationEntry() {
        return notificationEntry;
    }
}
