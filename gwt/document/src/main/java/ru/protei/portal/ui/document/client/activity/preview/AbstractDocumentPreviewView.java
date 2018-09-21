package ru.protei.portal.ui.document.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractDocumentPreviewView extends IsWidget {

    void setActivity(AbstractDocumentPreviewActivity activity);

    void setHeader(String header);

    void setName(String name);

    void setVersion(String text);

    void setCreatedDate(String created);

    void setType(String type);

    void setAnnotation(String annotation);

    void setProject(String project);

    void setManager(String manager);

    void setRegistrar(String text);

    void setContractor(String text);

    void setNumberDecimal(String numberDecimal);

    void setNumberInventory(String numberInventory);

    void setKeyWords(String keyWords);

    void setDownloadLink(String link);

    void setExecutionType(String executionType);
}
