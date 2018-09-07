package ru.protei.portal.core.model.yt;

/**
 * Created by admin on 22/11/2017.
 */
public class Link {
    private String value;
    private String type;
    private String role;

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole( String role ) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Link{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
