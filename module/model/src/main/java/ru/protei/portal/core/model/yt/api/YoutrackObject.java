package ru.protei.portal.core.model.yt.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.yt.api.issue.SimpleIssueCustomField;
import ru.protei.portal.core.model.yt.api.issue.SingleEnumIssueCustomField;
import ru.protei.portal.core.model.yt.api.project.SimpleProjectCustomField;
import ru.protei.portal.core.model.yt.api.value.EnumBundleElement;
import ru.protei.portal.core.model.yt.api.project.EnumProjectCustomField;
import ru.protei.portal.core.model.yt.api.value.StateBundleElement;
import ru.protei.portal.core.model.yt.api.value.TextFieldValue;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "$type", visible = true, defaultImpl = Void.class)
@JsonSubTypes( {
        @JsonSubTypes.Type( name = YoutrackObject.Issue.simpleIssueCustomField, value = SimpleIssueCustomField.class ),
        @JsonSubTypes.Type( name = YoutrackObject.Issue.singleEnumIssueCustomField, value = SingleEnumIssueCustomField.class ),
        @JsonSubTypes.Type( name = YoutrackObject.Project.enumProjectCustomField, value = EnumProjectCustomField.class ),
        @JsonSubTypes.Type( name = YoutrackObject.Project.SimpleProjectCustomField, value = SimpleProjectCustomField.class ),
        @JsonSubTypes.Type( name = YoutrackObject.Value.EnumBundleElement, value = EnumBundleElement.class ),
        @JsonSubTypes.Type( name = YoutrackObject.Value.StateBundleElement, value = StateBundleElement.class ),
        @JsonSubTypes.Type( name = YoutrackObject.Value.TextFieldValue, value = TextFieldValue.class )


} )
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutrackObject {

    public interface Issue {
        String simpleIssueCustomField = "SimpleIssueCustomField";
        String singleEnumIssueCustomField = "SingleEnumIssueCustomField";
    }
    public interface Project {
        String enumProjectCustomField = "EnumProjectCustomField";
        String SimpleProjectCustomField = "SimpleProjectCustomField";
    }
    public interface Value {
        String EnumBundleElement = "EnumBundleElement";
        String StateBundleElement = "StateBundleElement";
        String TextFieldValue = "TextFieldValue";
    }

    public String $type;
    public String id;

    @Override
    public String toString() {
        return "YoutrackObject{" +
                "$type='" + $type + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
