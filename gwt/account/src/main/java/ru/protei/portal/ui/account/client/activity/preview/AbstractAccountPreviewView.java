package ru.protei.portal.ui.account.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractAccountPreviewView extends IsWidget {
    void setActivity( AbstractAccountPreviewActivity activity );
    void setLogin( String value );
    void setLastName( String value );
    void setFirstName( String value );
    void setSecondName( String value );
    void setCompany ( String value );
    void setRoles( String value );
}
