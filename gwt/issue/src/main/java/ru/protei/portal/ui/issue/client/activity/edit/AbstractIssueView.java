package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.HasWidgets;

public interface AbstractIssueView {

    void setPrivateIssue( boolean privateIssue );

    void setCaseNumber( Long caseNumber );

    HasWidgets getTagsContainer();

    void setCreatedBy( String value );

    HasWidgets getInfoContainer();

    HasWidgets getMetaContainer();

}
