package ru.protei.portal.core.model.yt.fields.issue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.yt.fields.YtFields;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "name", visible = true, defaultImpl = Void.class)
@JsonSubTypes( {
        @JsonSubTypes.Type( name = YtFields.updated, value = DateIssueField.class ),
        @JsonSubTypes.Type( name = YtFields.stateEng, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = YtFields.stateRus, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = YtFields.equipmentStateRus, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = YtFields.acrmStateRus, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = YtFields.updaterName, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = YtFields.priority, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = YtFields.description, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = YtFields.summary, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = YtFields.crmNumber, value = StringArrayWithIdArrayIssueField.class ),
} )
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueField {
    public String name;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IssueField{" +
                "name='" + name + '\'' +
                '}';
    }
}
