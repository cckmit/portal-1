package ru.protei.portal.core.model.struct;

public class UserLoginPassword {

    private String login;

    private String password;

    private String displayName;

    public UserLoginPassword() {}

    public UserLoginPassword(String login, String password, String displayName) {
        this.login = login;
        this.password = password;
        this.displayName = displayName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
