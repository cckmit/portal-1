package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemActivity;
import ru.protei.portal.ui.company.client.view.edit.CompanyView;

/**
 * Created by bondarenko on 21.10.16.
 */
public interface AbstractCompanyView  extends IsWidget {

    void setActivity( AbstractCompanyActivity activity );

    void setCompanyNameStatus(CompanyView.CompanyNameStatus status);
    HasText companyName();
    String getActualAddress();
    String getLegalAddress();

    String getWebSite();
    String getComment();

    void setActualAddress(String val);
    void setLegalAddress(String val);
    void setWebSite(String val);
    void setComment(String val);


}
