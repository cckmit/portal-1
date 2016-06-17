package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 16.06.16.
 */
public enum En_AuthType {

    LOCAL(1),
    LDAP(2);

    private En_AuthType (int id) {
        this.id = id;
    }

    private final int id;

    public int getId() {
        return id;
    }
}
