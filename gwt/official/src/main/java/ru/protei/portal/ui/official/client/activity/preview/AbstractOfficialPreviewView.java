package ru.protei.portal.ui.official.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция активности на странице превью должностных лиц
 */
public interface AbstractOfficialPreviewView extends IsWidget{

    void setActivity( AbstractOfficialPreviewActivity activity );

    void setCreationDate( String value );
    void setProduct( String value );
    void setRegion( String value );
    void setInfo( String value);
    void showFullScreen(boolean value);

}
