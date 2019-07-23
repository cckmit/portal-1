package ru.protei.portal.ui.company.client.activity.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.core.model.ent.CaseTag;

import java.util.Set;

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

    void setCompanyLinksMessage(String value );

    void setInfo( String value );

    Widget asWidget(boolean isForTableView);

    HasWidgets getContactsContainer();

    HasWidgets getSiteFolderContainer();

    HasVisibility getContactsContainerVisibility();

    HasVisibility getSiteFolderContainerVisibility();

    void setSubscriptionEmails(String value);

    void setTags(Set<CaseTag> value);
}
