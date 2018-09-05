package ru.protei.portal.core.model.yt.fields;

import java.util.List;

public class StringArrayWithIdArrayField extends Field {
    private List<String> value;
    private List<String> valueId;

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public List<String> getValueId() {
        return valueId;
    }

    public void setValueId(List<String> valueId) {
        this.valueId = valueId;
    }

    @Override
    public String toString() {
        return "StringArrayWithIdArrayField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", valueId=" + valueId +
                '}';
    }
}
