package ru.protei.portal.core.model.yt.fields;

import ru.protei.portal.core.model.yt.Person;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class PersonArrayField extends Field {
    protected List<Person> value;

    public List<Person> getValue() {
        return value;
    }

    public void setValue( List<Person> value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PersonArrayField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
