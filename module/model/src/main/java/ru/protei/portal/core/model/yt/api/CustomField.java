package ru.protei.portal.core.model.yt.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.yt.api.issue.SimpleIssueCustomField;
import ru.protei.portal.core.model.yt.api.issue.SingleEnumIssueCustomField;
import ru.protei.portal.core.model.yt.api.value.EnumBundleElement;
import ru.protei.portal.core.model.yt.api.project.EnumProjectCustomField;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "$type", visible = true, defaultImpl = Void.class)
@JsonSubTypes( {
        @JsonSubTypes.Type( name = CustomField.Issue.simpleIssueCustomField, value = SimpleIssueCustomField.class ),
        @JsonSubTypes.Type( name = CustomField.Issue.singleEnumIssueCustomField, value = SingleEnumIssueCustomField.class ),
        @JsonSubTypes.Type( name = CustomField.Project.enumProjectCustomField, value = EnumProjectCustomField.class ),
        @JsonSubTypes.Type( name = CustomField.Value.EnumBundleElement, value = EnumBundleElement.class )


} )
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomField {

    public interface Issue {
        String simpleIssueCustomField = "SimpleIssueCustomField";
        String singleEnumIssueCustomField = "SingleEnumIssueCustomField";
    }
    public interface Project {
        String enumProjectCustomField = "EnumProjectCustomField";
    }
    public interface Value {
        String EnumBundleElement = "EnumBundleElement";
    }

    public String $type;
    public String id;
    public String name; //TODO нет у ProjectCustomField

//    public String getId() {
//        return id;
//    }
//
//    public void setId( String id ) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName( String name ) {
//        this.name = name;
//    }
//
//    public String get$type() {
//        return $type;
//    }
//
//    public void set$type( String $type ) {
//        this.$type = $type;
//    }

    @Override
    public String toString() {
        return "CustomField{" +
                "$type='" + $type + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
