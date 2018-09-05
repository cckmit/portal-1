package ru.protei.portal.core.model.yt.fields;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class StringField extends Field {
    protected String value;

    protected List<String> oldValue;

    protected List<String> newValue;

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public List<String> getOldValue() {
        return oldValue;
    }

    public void setOldValue(List<String> oldValue) {
        this.oldValue = oldValue;
    }

    public List<String> getNewValue() {
        return newValue;
    }

    public void setNewValue(List<String> newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "StringField{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }
}
