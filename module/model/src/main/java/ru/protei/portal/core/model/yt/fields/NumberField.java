package ru.protei.portal.core.model.yt.fields;

/**
 * Created by admin on 15/11/2017.
 */
public class NumberField extends Field {
    protected Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue( Integer value ) {
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
