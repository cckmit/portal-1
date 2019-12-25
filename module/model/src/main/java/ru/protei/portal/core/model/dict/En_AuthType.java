package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 16.06.16.
 */
public enum En_AuthType {

    LOCAL(1),
    LDAP(2);

    En_AuthType (int id) {
        this.id = id;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static En_AuthType find (int id) {
        for (En_AuthType at : En_AuthType.values())
            if (at.id == id)
                return at;

        return null;
    }

    public String getImageSrc() {
        return "./images/auth_" + name().toLowerCase() + ".png";
    }

}
