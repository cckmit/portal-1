package ru.protei.portal.core.model.yt.fields.issue;

public class BooleanIssueField extends IssueField {
    private Boolean value;

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BooleanIssueField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
