package ru.protei.portal.core.model.yt.fields.issue;


import ru.protei.portal.core.model.yt.YtAttachment;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
public class AttachmentArrayIssueField extends IssueField {
    protected List<YtAttachment> value;

    public List<YtAttachment> getValue() {
        return value;
    }

    public void setValue( List<YtAttachment> value ) {
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
