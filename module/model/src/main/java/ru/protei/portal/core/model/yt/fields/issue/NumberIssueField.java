package ru.protei.portal.core.model.yt.fields.issue;

/**
 * Created by admin on 15/11/2017.
 */
public class NumberIssueField extends IssueField {
    protected Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue( Integer value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NumberIssueField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
