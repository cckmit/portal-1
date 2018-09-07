package ru.protei.portal.core.model.yt.fields.issue;

import ru.protei.portal.core.model.yt.Link;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class LinkArrayIssueField extends IssueField {
    protected List<Link> value;

    public List<Link> getValue() {
        return value;
    }

    public void setValue( List<Link> value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LinkArrayIssueField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
