package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractIssueView extends IsWidget {

    void setPrivateIssue( boolean privateIssue );

    void setCaseNumber( Long caseNumber );

    HasWidgets getTagsContainer();

    void setCreatedBy( String value );

    HasWidgets getInfoContainer();

    HasWidgets getMetaContainer();

    HasWidgets getLinksContainer();

    void setName( String issueName );

    void setNameVisible( boolean isNameVisible );
}
