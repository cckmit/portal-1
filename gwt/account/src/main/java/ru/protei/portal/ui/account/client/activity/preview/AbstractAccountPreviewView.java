package ru.protei.portal.ui.account.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractAccountPreviewView extends IsWidget {
    void setActivity( AbstractAccountPreviewActivity activity );
    void setLogin( String value );
    void setRoles( String value );

    void setPersonInfo(String value);

    void setTypeImage(String value);
}
