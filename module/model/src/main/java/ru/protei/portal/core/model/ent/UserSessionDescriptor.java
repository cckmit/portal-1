package ru.protei.portal.core.model.ent;


/**
 * Created by michael on 23.06.16.
 */
public class UserSessionDescriptor {

    UserSession session;
    UserLogin login;
    UserRole urole;
    Company company;
    Person person;

    public UserSessionDescriptor() {

    }

    public void init (UserSession session) {
        this.session = session;
    }

    public void login (UserLogin login, UserRole role, Person p, Company c) {
        this.login = login;
        this.urole = role;
        this.company = c;
        this.person = p;
    }

    public void close () {
        this.session = null;
        this.urole = null;
        this.login = null;
        this.company = null;
        this.person = null;
    }

    public int getTimeToLive () {
        return this.session == null ? -1
                : this.session.getExpired() == null ? 0
                : (int)((this.session.getExpired().getTime()-System.currentTimeMillis())/1000L);
    }

    public String getSessionId () {
        return this.session != null ? this.session.getSessionId() : null;
    }

    public UserSession getSession() {
        return session;
    }

    public UserLogin getLogin() {
        return login;
    }

    public UserRole getUrole() {
        return urole;
    }

    public Company getCompany() {
        return company;
    }

    public Person getPerson() {
        return person;
    }

    public boolean isValid () {
        return this.session != null && this.login != null && this.urole != null;
    }

    public boolean isExpired () {
        return this.session != null && this.session.checkIsExpired();
    }
}
