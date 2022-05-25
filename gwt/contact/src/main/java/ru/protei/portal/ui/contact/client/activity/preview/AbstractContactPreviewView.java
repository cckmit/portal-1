package ru.protei.portal.ui.contact.client.activity.preview;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью контакта
 */
public interface AbstractContactPreviewView extends IsWidget {

    void setActivity( AbstractContactPreviewActivity activity );

    void setDisplayName ( String value);

    void setDisplayNameHref(String link);

    void setBirthday(String value );
    void setLogins( String value );
    void setInfo( String value );

    void setCompany ( String value );
    void setPosition( String value );

    void setPhone( String value );
    void setEmail( String value);
    void setAddress( String value );
    void setHomeAddress( String value );

    HasVisibility firedMsgVisibility();
    HasVisibility deletedMsgVisibility();

    void setGenderImage(String icon);

    void showFullScreen(boolean isFullScreen);

}
