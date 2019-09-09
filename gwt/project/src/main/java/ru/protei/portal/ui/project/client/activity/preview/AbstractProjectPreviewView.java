package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида проекта
 */
public interface AbstractProjectPreviewView extends IsWidget {

    void setActivity( AbstractProjectPreviewActivity activity );

    void watchForScroll( boolean isWatch );

    void setAuthor(String value);

    void setCreationDate( String value );

    void setName( String name );

    void setState( long value );

    void setDirection( String value );

    void setTeam( String value );

    void setDescription( String value );

    void setRegion( String value );

    void setProducts( String value );

    void setCompany( String value );

    void setCustomerType( String value );

    void showFullScreen(boolean value );

    HasWidgets getCommentsContainer();
    HasWidgets getDocumentsContainer();
}
