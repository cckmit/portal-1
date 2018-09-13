package ru.protei.portal.core.model.yt.fields.issue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.yt.fields.Fields;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "name", visible = true, defaultImpl = Void.class)
@JsonSubTypes( {
        @JsonSubTypes.Type( name = Fields.created, value = DateIssueField.class ),
        @JsonSubTypes.Type( name = Fields.updated, value = DateIssueField.class ),
        @JsonSubTypes.Type( name = Fields.resolved, value = DateIssueField.class ),
        @JsonSubTypes.Type( name = Fields.numberInProject, value = NumberIssueField.class ),
        @JsonSubTypes.Type( name = Fields.commentsCount, value = NumberIssueField.class ),
        @JsonSubTypes.Type( name = Fields.votes, value = NumberIssueField.class ),
        @JsonSubTypes.Type( name = Fields.links, value = LinkArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.priority, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.type, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.stateEng, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.sprintRus, value = StringArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.sprintEng, value = IdValueArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.SP, value = StringArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.projectShortName, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = Fields.summary, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = Fields.description, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = Fields.updaterName, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = Fields.updaterFullName, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = Fields.reporterName, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = Fields.reporterFullName, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = Fields.assignee, value = PersonArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.reviewer, value = PersonArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.customer, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.subsystemEng, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.subsystemRus, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = Fields.crmIssueNumber, value = StringArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.attachments, value = AttachmentArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.evaluation, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.component, value = StringArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.elapsedTime, value = StringArrayWithIdArrayIssueField.class ),
        @JsonSubTypes.Type( name = Fields.executor, value = StringIssueField.class ),
        @JsonSubTypes.Type( name = Fields.markdown, value = BooleanIssueField.class ),
        @JsonSubTypes.Type( name = Fields.wikified, value = BooleanIssueField.class ),
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
