package ru.protei.portal.core.model.yt.fields;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.yt.IssueFields;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "name", visible = true, defaultImpl = StringField.class)
@JsonSubTypes( {
        @JsonSubTypes.Type( name = IssueFields.created, value = DateField.class ),
        @JsonSubTypes.Type( name = IssueFields.updated, value = DateField.class ),
        @JsonSubTypes.Type( name = IssueFields.resolved, value = DateField.class ),
        @JsonSubTypes.Type( name = IssueFields.numberInProject, value = NumberField.class ),
        @JsonSubTypes.Type( name = IssueFields.commentsCount, value = NumberField.class ),
        @JsonSubTypes.Type( name = IssueFields.votes, value = NumberField.class ),
        @JsonSubTypes.Type( name = IssueFields.links, value = LinkArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.priority, value = StringArrayWithIdArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.type, value = StringArrayWithIdArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.state, value = StringArrayWithIdArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.sprintRus, value = StringArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.sprintEng, value = IdValueArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.SP, value = StringArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.projectShortName, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.summary, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.description, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.updaterName, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.updaterFullName, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.reporterName, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.reporterFullName, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.assignee, value = PersonArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.reviewer, value = PersonArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.customer, value = StringArrayWithIdArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.subsystemEng, value = StringArrayWithIdArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.subsystemRus, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.crmIssueNumber, value = StringArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.attachments, value = AttachmentArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.evalution, value = StringArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.component, value = StringArrayField.class ),
        @JsonSubTypes.Type( name = IssueFields.elapsedTime, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.executor, value = StringField.class ),
        @JsonSubTypes.Type( name = IssueFields.markdown, value = BooleanField.class ),
        @JsonSubTypes.Type( name = IssueFields.wikified, value = BooleanField.class ),
} )
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {
    public String name;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                '}';
    }
}
