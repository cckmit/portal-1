package ru.protei.portal.ui.document.client.activity.preview;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;

public interface AbstractDocumentPreviewView extends IsWidget {

    void setActivity(AbstractDocumentPreviewActivity activity);

    void setHeader(String header);

    void setVersion(String text);

    void setCreatedBy(String created);

    void setType(String type);

    void setAnnotation(String annotation);

    void setProject(String project);

    void setManager(String manager);

    void setRegistrar(String text);

    void setContractor(String text);

    void setMembers(String text);

    void setNumberDecimal(String numberDecimal);

    void setNumberInventory(String numberInventory);

    void setKeyWords(String keyWords);

    void setDownloadLinkPdf(String link);

    void setDownloadLinkDoc(String link);

    void setExecutionType(String executionType);

    AbstractDocumentUploader documentDocUploader();

    HasValue<String> documentDocComment();

    HasVisibility documentDocVisibility();

    HasVisibility documentDocUploadContainerLoading();

    HasVisibility footerVisibility();
}
