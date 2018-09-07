package ru.protei.portal.core.model.yt.fields.issue;


import ru.protei.portal.core.model.yt.Attachment;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class AttachmentArrayIssueField extends IssueField {
    protected List<Attachment> value;

    public List<Attachment> getValue() {
        return value;
    }

    public void setValue( List<Attachment> value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AttachmentArrayIssueField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
