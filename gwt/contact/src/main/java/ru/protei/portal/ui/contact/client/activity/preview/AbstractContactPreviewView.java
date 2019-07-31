package ru.protei.portal.ui.contact.client.activity.preview;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

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

    void setPhone( String value );
    void setEmail( List<String> emailList, List<String> mailtoList);
    void setAddress( String value );
    void setHomeAddress( String value );

    void showFullScreen ( boolean value );
    HTMLPanel preview ();

    HasVisibility firedMsgVisibility();
    HasVisibility deletedMsgVisibility();
}
