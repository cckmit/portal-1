package ru.protei.portal.core.model.yt.fields;

import ru.protei.portal.core.model.yt.IdValue;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class IdValueArrayField extends Field {
    protected List<IdValue> value;

    public List<IdValue> getValue() {
        return value;
    }

    public void setValue( List<IdValue> value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "IdValueArrayField{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
