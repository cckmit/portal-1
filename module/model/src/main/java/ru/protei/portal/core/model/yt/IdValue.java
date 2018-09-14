package ru.protei.portal.core.model.yt;

/**
 * Created by admin on 22/11/2017.
 */
public class IdValue {
    private String value;
    private String id;

    @Override
    public String toString() {
        return "IdValue{" +
                "value='" + value + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }
}
