package ru.protei.portal.ui.contact.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью контакта
 */
public interface AbstractContactPreviewView extends IsWidget {

    void setActivity( AbstractContactPreviewActivity activity );

    void setLastName( String value );

    void setFirstName( String value );

    void setSecondName( String value );

    void setGender ( String value );

    void setBirthday( String value );

    void setPosition( String value );

    void setDepartment( String value );

    void setPhone( String value );

    void setFax( String value );

    void setEmail( String value );

    void setAddress( String value );

    void setInfo( String value );

    void setLinkToPreview ( String value );

}
