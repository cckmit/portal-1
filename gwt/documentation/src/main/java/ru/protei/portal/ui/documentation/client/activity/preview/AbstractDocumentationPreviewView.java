package ru.protei.portal.ui.documentation.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractDocumentationPreviewView extends IsWidget {

    void setActivity(AbstractDocumentationPreviewActivity activity);

    void setHeader(String header);

    void setName(String name);

    void setCreatedDate(String created);

    void setType(String type);

    void setAnnotation(String annotation);

    void setProject(String project);

    void setManager(String manager);

    void setNumberDecimal(String numberDecimal);

    void setNumberInventory(String numberInventory);

    void setKeyWords(String keyWords);

    void setCopyBtnEnabledStyle(boolean isEnabled);

    void setRemoveBtnEnabledStyle(boolean isEnabled);
}
