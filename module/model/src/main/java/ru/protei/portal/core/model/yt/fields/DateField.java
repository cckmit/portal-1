package ru.protei.portal.core.model.yt.fields;

import java.util.Date;

/**
 * Created by admin on 15/11/2017.
 */
public class DateField extends Field {
    protected Date value;

    public Date getValue() {
        return value;
    }

    public void setValue( Date value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DateField{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
