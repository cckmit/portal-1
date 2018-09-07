package ru.protei.portal.core.model.yt.fields.issue;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class StringArrayIssueField extends IssueField {
    protected List<String> value;

    protected List<String> oldValue;

    protected List<String> newValue;

    public List<String> getValue() {
        return value;
    }

    public void setValue( List<String> value ) {
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
        return "StringArrayIssueField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }
}
