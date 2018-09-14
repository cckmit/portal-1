package ru.protei.portal.core.model.yt.fields.issue;

import ru.protei.portal.core.model.yt.IdValue;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class IdValueArrayIssueField extends IssueField {
    protected List<IdValue> value;

    public List<IdValue> getValue() {
        return value;
    }

    public void setValue( List<IdValue> value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "IdValueArrayIssueField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
