package ru.protei.portal.core.model.yt.fields;

public class BooleanField extends Field {
    private Boolean value;

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BooleanField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
