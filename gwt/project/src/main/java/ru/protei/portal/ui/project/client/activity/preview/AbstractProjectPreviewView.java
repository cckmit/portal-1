package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида проекта
 */
public interface AbstractProjectPreviewView extends IsWidget {

    void setActivity( AbstractProjectPreviewActivity activity );

    void setName(String value);

    void setCreatedBy(String value );

    void setHeader(String name );

    void setState( long value );

    void setDirection( String value );

    void setTeam( String value );

    void setDescription( String value );

    void setRegion( String value );

    void setProduct(String value );

    void setCompany( String value );

    void setCustomerType( String value );

    void setContract(String value, String link);

    void setPlatform(String value, String link);

    void isFullScreen(boolean isFullScreen);

    void setTechnicalSupportValidity(String value);

    HasVisibility backButtonVisibility();

    HasWidgets getCommentsContainer();
    HasWidgets getDocumentsContainer();
    HasWidgets getLinksContainer();


}
