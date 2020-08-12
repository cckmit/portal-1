package ru.protei.portal.ui.common.client.activity.attachment;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

/**
 * Created by bondarenko on 28.12.16.
 */
public interface AbstractAttachmentView extends IsWidget {
    void setActivity( AbstractAttachmentActivity activity );

    void setPicture(String url);
    void setFileName(String fileName);
    void setFileSize(long B);
    void setDownloadUrl(String url);
    void setCreationInfo(String author, Date created);
    HasVisibility removeButtonVisibility();
}
