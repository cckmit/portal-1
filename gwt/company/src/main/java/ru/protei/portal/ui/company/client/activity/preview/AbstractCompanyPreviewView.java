package ru.protei.portal.ui.company.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Абстракция представления превью компании
 */
public interface AbstractCompanyPreviewView extends IsWidget {

    void setName( String name );

    void setActivity( AbstractCompanyPreviewActivity activity );

    void watchForScroll(boolean isWatch);

    void setPhone( String value );

    void setSite( String value );

    void setEmail( String value );

    void setAddressDejure( String value );

    void setAddressFact( String value );

    void setCategory( String value );

    void setGroupCompany( String value );

    void setInfo( String value );

    void setGroupVisible( boolean value );

    Widget asWidget(boolean isForTableView);

    HasWidgets getContactsContainer();

    void setSubscriptionEmails(String value);
}
