package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.yt.fields.Fields;
import ru.protei.portal.core.model.yt.fields.issue.DateIssueField;
import ru.protei.portal.core.model.yt.fields.issue.IssueField;
import ru.protei.portal.core.model.yt.fields.issue.StringArrayWithIdArrayIssueField;
import ru.protei.portal.core.model.yt.fields.issue.StringIssueField;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by admin on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    private String id;
    private String entityId;
    private List<IssueField> field;
    private List<Comment> comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public List<IssueField> getField() {
        return field;
    }

    public void setField(List<IssueField> field) {
        this.field = field;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    private <T extends IssueField> T getField(String name) {
        if (field == null || name == null) {
            return null;
        }

        IssueField fieldValue = field.stream()
                .filter(Objects::nonNull)
                .filter((field) -> name.equals(field.getName()))
                .findFirst()
                .orElse(null);

        if (fieldValue == null)
            return null;
        return (T) fieldValue;
    }

    public String getProjectShortName() {
        return getStringFieldValue(Fields.projectShortName);
    }

    public String getSummary() {
        return getStringFieldValue(Fields.summary);
    }

    public String getDescription() {
        return getStringFieldValue(Fields.description);
    }

    public Date getCreated() {
        DateIssueField field = getField(Fields.created);
        return field == null ? null : field.getValue();
    }

    public Date getUpdated() {
        DateIssueField field = getField(Fields.updated);
        return field == null ? null : field.getValue();
    }

    public String getUpdaterName() {
        return getStringFieldValue(Fields.updaterName);
    }

    public String getStateId() {
        StringArrayWithIdArrayIssueField field = getField(Fields.state);
        if (field == null)
            return null;
        List<String> valueId = field.getValueId();
        if (CollectionUtils.isEmpty(valueId))
            return null;
        return valueId.get(0);
    }

    private String getStringFieldValue(String name) {
        StringIssueField field = getField(name);
        return field == null ? null : field.getValue();
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id='" + id + '\'' +
                ", entityId='" + entityId + '\'' +
                ", field=" + field +
                ", comment=" + comment +
                '}';
    }
}
