package ru.protei.portal.core.model.yt.fields.change;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.yt.fields.Fields;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "name", visible = true, defaultImpl = Void.class)
@JsonSubTypes( {
        @JsonSubTypes.Type( name = Fields.priority, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.type, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.stateEng, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.stateRus, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.equipmentStateRus, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.updaterName, value = StringChangeField.class ),
        @JsonSubTypes.Type( name = Fields.updated, value = DateChangeField.class ),
        @JsonSubTypes.Type( name = Fields.assignee, value = PersonArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.reviewer, value = PersonArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.customer, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.sprintRus, value = StringArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.sprintEng, value = IdValueArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.subsystemEng, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.subsystemRus, value = StringOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.attachments, value = AttachmentArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.evaluation, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.elapsedTime, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = Fields.executor, value = StringOldNewChangeField.class ),
} )
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeField {
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ChangeField{" +
                "name='" + name + '\'' +
                '}';
    }
}
