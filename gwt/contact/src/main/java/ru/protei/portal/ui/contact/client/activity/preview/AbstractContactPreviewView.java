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
    void setDisplayName ( String value );
    void setShortName ( String value );

    void setGender ( String value );
    void setBirthday( String value );
    void setInfo( String value );

    void setCompany ( String value );
    void setPosition( String value );
    void setDepartment( String value );

    void setWorkPhone( String value );
    void setPersonalPhone( String value );
    void setWorkFax( String value );
    void setPersonalFax( String value );
    void setWorkEmail( String value );
    void setPersonalEmail( String value );
    void setWorkAddress( String value );
    void setPersonalAddress( String value );

    void setLinkToPreview ( String value );
}
