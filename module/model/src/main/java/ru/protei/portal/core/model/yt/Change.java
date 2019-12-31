package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.yt.fields.YtFields;
import ru.protei.portal.core.model.yt.fields.change.ChangeField;
import ru.protei.portal.core.model.yt.fields.change.DateChangeField;
import ru.protei.portal.core.model.yt.fields.change.StringArrayWithIdArrayOldNewChangeField;
import ru.protei.portal.core.model.yt.fields.change.StringChangeField;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class Change {

    private List<ChangeField> field;
    private List<Comment> comment;

    public List<ChangeField> getField() {
        return field;
    }

    public void setField(List<ChangeField> field ) {
        this.field = field;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    private <T extends ChangeField> T getField(String name) {
        if (field == null || name == null) {
            return null;
        }

        ChangeField fieldValue = field.stream()
                .filter(Objects::nonNull)
                .filter((field) -> name.equals(field.getName()))
                .findFirst()
                .orElse(null);

        if (fieldValue == null)
            return null;
        return (T) fieldValue;
    }

    public Date getUpdated() {
        DateChangeField dateChangeField = getField(YtFields.updated);
        if (dateChangeField == null)
            return null;
        return dateChangeField.getValue();
    }

    public String getUpdaterName() {
        StringChangeField stringChangeField = getField(YtFields.updaterName);
        if (stringChangeField == null)
            return null;
        return stringChangeField.getValue();
    }

    public StringArrayWithIdArrayOldNewChangeField getStateChangeField() {
        return HelperFunc.nvlt(
                getField(YtFields.stateEng),
                getField(YtFields.stateRus),
                getField(YtFields.equipmentStateRus),
                getField(YtFields.acrmStateRus)
        );
    }

    @Override
    public String toString() {
        return "Change{" +
                "field=" + field +
                ", comment=" + comment +
                '}';
    }
}
