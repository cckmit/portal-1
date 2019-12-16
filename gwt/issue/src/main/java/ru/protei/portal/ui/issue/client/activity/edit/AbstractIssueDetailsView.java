package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;

public interface AbstractIssueDetailsView extends IsWidget {

    HasWidgets getNameInfoContainer();
    HasWidgets getMetaContainer();

    HasWidgets getCommentsContainer();

    HasAttachments attachmentsContainer();

    HasWidgets getTagsContainer();

    HasWidgets getLinksContainer();

    void setPrivateIssue( boolean privateIssue );

    void setCaseNumber( Long caseNumber );

    void setCreatedBy( String value );


    HasVisibility editNameAndDescriptionButtonVisibility();

}
