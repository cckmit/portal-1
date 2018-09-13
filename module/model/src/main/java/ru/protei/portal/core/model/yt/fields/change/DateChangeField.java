package ru.protei.portal.core.model.yt.fields.change;

import java.util.Date;

public class DateChangeField extends ChangeField {
    private Date value;

    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DateChangeField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
