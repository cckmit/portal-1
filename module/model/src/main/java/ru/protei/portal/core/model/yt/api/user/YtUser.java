package ru.protei.portal.core.model.yt.api.user;

import ru.protei.portal.core.model.yt.api.YtDto;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-User.html */
public class YtUser extends YtDto {

    public String login;
    public String email;
    public String fullName;
    public String avatarUrl;

    @Override
    public String toString() {
        return "YtUser{" +
                "login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
