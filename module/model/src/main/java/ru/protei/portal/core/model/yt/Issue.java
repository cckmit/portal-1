package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.yt.fields.YtFields;
import ru.protei.portal.core.model.yt.fields.issue.IssueField;
import ru.protei.portal.core.model.yt.fields.issue.StringArrayWithIdArrayIssueField;
import ru.protei.portal.core.model.yt.fields.issue.StringIssueField;

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

    public String getStateId() {
        StringArrayWithIdArrayIssueField field = getStateField();
        return fromStringArrayField( field );
    }

    private String fromStringArrayField( StringArrayWithIdArrayIssueField field ) {
        if (field == null)
            return null;
        List<String> valueId = field.getValueId();
        if (CollectionUtils.isEmpty(valueId))
            return null;
        return valueId.get(0);
    }

    private String fromStringField( StringIssueField field ) {
        if (field == null)
            return null;
        return field.getValue();
    }

    private StringArrayWithIdArrayIssueField getStateField() {
        return HelperFunc.nvlt(
                getField(YtFields.stateEng),
                getField(YtFields.stateRus),
                getField(YtFields.equipmentStateRus),
                getField(YtFields.acrmStateRus)
        );
    }

    public String getSummary() {
        return fromStringField( getField( YtFields.summary ) );
    }

    public String getDescription() {
        return fromStringField( getField( YtFields.description ) );
    }

    public String getPriority() {
        return fromStringArrayField( getField( YtFields.priority ) );
    }

    public Long getCrmNumber() {
        return NumberUtils.parseLong( fromStringArrayField( getField( YtFields.crmNumber ) ) );
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
