package ru.protei.portal.core.model.yt;

/**
 * Created by admin on 22/11/2017.
 */
public class Person {
    private String value;
    private String fullName;

    @Override
    public String toString() {
        return "Person{" +
                "value='" + value + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }
}
