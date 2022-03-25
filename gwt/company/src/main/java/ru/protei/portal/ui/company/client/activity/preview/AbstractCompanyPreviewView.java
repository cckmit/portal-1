package ru.protei.portal.ui.company.client.activity.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * Абстракция представления превью компании
 */
public interface AbstractCompanyPreviewView extends IsWidget {

    void setName( String name );

    void setActivity( AbstractCompanyPreviewActivity activity );

    void setPhone( String value );

    void setSite( String value );

    void setEmail( List<Widget> value );

    void setAddressDejure( String value );

    void setAddressFact( String value );

    void setCategory( String value );

    void setCompanyLinksMessage(String value );

    void setInfo( String value );

    Widget asWidget(boolean isForTableView);

    HasWidgets getContactsContainer();

    HasWidgets getSiteFolderContainer();
    
    void setCommonManager(String value);

    HasVisibility getContactsContainerVisibility();

    HasVisibility getSiteFolderContainerVisibility();

    void setSubscriptionEmails(String value);
}
