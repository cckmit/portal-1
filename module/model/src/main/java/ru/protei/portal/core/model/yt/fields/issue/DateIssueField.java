package ru.protei.portal.core.model.yt.fields.issue;

import java.util.Date;

/**
 * Created by admin on 15/11/2017.
 */
public class DateIssueField extends IssueField {
    protected Date value;

    public Date getValue() {
        return value;
    }

    public void setValue( Date value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DateIssueField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
