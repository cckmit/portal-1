package ru.protei.portal.core.model.yt.fields.change;

public class StringChangeField extends ChangeField {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StringChangeField{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
