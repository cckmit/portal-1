package ru.protei.portal.ui.official.client.activity.preview;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;

/**
 * Абстрактное представление карточки должностных лиц
 */
public interface AbstractOfficialPreviewView extends IsWidget{

    void setActivity( AbstractOfficialPreviewActivity activity );

    void setCreationDate( String value );
    void setProduct( String value );
    void setRegion( String value );
    void setInfo( String value);
    void showFullScreen(boolean value);

    void clearMembers();

    HTMLPanel getMembersContainer();

    HasAttachments attachmentsContainer();
    HasWidgets getCommentsContainer();

    void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler);
}
