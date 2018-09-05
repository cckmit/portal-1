package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.yt.fields.DateField;
import ru.protei.portal.core.model.yt.fields.Field;
import ru.protei.portal.core.model.yt.fields.StringArrayWithIdArrayField;
import ru.protei.portal.core.model.yt.fields.StringField;

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
    private List<Field> field;
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

    public List<Field> getField() {
        return field;
    }

    public void setField(List<Field> field) {
        this.field = field;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    private <T extends Field> T getField(String name) {
        if (field == null || name == null) {
            return null;
        }

        Field fieldValue = field.stream()
                .filter(Objects::nonNull)
                .filter((field) -> name.equals(field.getName()))
                .findFirst()
                .orElse(null);

        if (fieldValue == null)
            return null;
        return (T) fieldValue;
    }

    public String getProjectShortName() {
        return getStringFieldValue(IssueFields.projectShortName);
    }

    public String getSummary() {
        return getStringFieldValue(IssueFields.summary);
    }

    public String getDescription() {
        return getStringFieldValue(IssueFields.description);
    }

    public Date getCreated() {
        DateField field = getField(IssueFields.created);
        return field == null ? null : field.getValue();
    }

    public Date getUpdated() {
        DateField field = getField(IssueFields.updated);
        return field == null ? null : field.getValue();
    }

    public String getUpdaterName() {
        return getStringFieldValue(IssueFields.updaterName);
    }

    private String getStringFieldValue(String name) {
        StringField field = getField(name);
        return field == null ? null : field.getValue();
    }

    private String getStateId() {
        StringArrayWithIdArrayField field = getField(IssueFields.state);
        if (field == null)
            return null;
        List<String> valueId = field.getValueId();
        if (CollectionUtils.isEmpty(valueId))
            return null;
        return valueId.get(0);
    }
}
