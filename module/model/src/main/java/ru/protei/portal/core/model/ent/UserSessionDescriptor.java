package ru.protei.portal.core.model.ent;


import ru.protei.portal.core.model.dict.En_Scope;

import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * Created by michael on 23.06.16.
 */
public class UserSessionDescriptor {

    UserSession session;
    UserLogin login;
    Company company;
    Person person;

    public UserSessionDescriptor() {

    }

    public void init (UserSession session) {
        this.session = session;
    }

    public void login (UserLogin login, Person p, Company c) {
        this.login = login;
        this.company = c;
        this.person = p;
    }


    public void close () {
        this.session = null;
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

    public Company getCompany() {
        return company;
    }

    public Person getPerson() {
        return person;
    }

    public boolean isValid () {
        return this.session != null && this.login != null && !this.login.getRoles().isEmpty();
    }

    public boolean isExpired () {
        return this.session != null && this.session.checkIsExpired();
    }

    public AuthToken makeAuthToken() {
        return new AuthToken( getSessionId(), session.getClientIp() );
    }

    public Collection<Long> getAllowedCompaniesIds() {
        if (company == null) return Collections.EMPTY_LIST;
        ArrayList<Long> ids = new ArrayList<>();
        ids.add( company.getId() );
        if (company.getChildCompaniesIds() != null)
            ids.addAll( company.getChildCompaniesIds() );
        return ids;
    }
}
