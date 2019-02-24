package ru.protei.portal.core.model.yt.fields.change;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.yt.fields.YtFields;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "name", visible = true, defaultImpl = Void.class)
@JsonSubTypes( {
        @JsonSubTypes.Type( name = YtFields.stateEng, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = YtFields.stateRus, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = YtFields.equipmentStateRus, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = YtFields.acrmStateRus, value = StringArrayWithIdArrayOldNewChangeField.class ),
        @JsonSubTypes.Type( name = YtFields.updaterName, value = StringChangeField.class ),
        @JsonSubTypes.Type( name = YtFields.updated, value = DateChangeField.class ),
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
