package ru.protei.portal.ui.official.client.activity.preview;

/**
 * Абстракция активности на странице превью должностных лиц
 */
public interface AbstractOfficialPreviewView {

    void setActivity( AbstractOfficialPreviewActivity activity );

    void setCreationDate( String value );
    void setProduct( String value );
    void setRegion( String value );
    void setInfo( String value);
    void showFullScreen(boolean value);

}
