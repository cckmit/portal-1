package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;

/**
 * Абстракция вида превью обращения
 */
public interface AbstractIssuePreviewView extends IsWidget {

    void setActivity( AbstractIssuePreviewActivity activity );

    void watchForScroll(boolean isWatch);
    void setPrivateIssue( boolean privateIssue );
    void setCaseId(Long caseId);
    void setHeader( String value );
    void setCreationDate( String value );
    void setState( long value );
    void setCriticality( int value );
    void setProduct( String value );
    void setCompany( String value );
    void setContact( String value );
    void setOurCompany( String value );
    void setManager( String value );
    void setInfo( String value );

    void showFullScreen( boolean value );

    HasWidgets getCommentsContainer();
    HasAttachments attachmentsContainer();

    void setFileUploadHandler(FileUploader.FileUploadHandler handler);
}
