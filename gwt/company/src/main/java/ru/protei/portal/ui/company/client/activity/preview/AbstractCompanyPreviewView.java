package ru.protei.portal.ui.company.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция представления превью компании
 */
public interface AbstractCompanyPreviewView extends IsWidget {

    void setActivity( AbstractCompanyPreviewActivity activity );

    void setPhone( String value );

    void setSite( String value );

    void setEmail( String value );

    void setAddressDejure( String value );

    void setAddressFact( String value );

    void setGroupCompany( String value );

    void setInfo( String value );
}
