package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.client.service.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Created by bondarenko on 21.10.16.
 */
public interface AbstractCompanyEditView extends IsWidget {

    void setActivity( AbstractCompanyEditActivity activity );

    void setCompanyNameStatus(NameStatus status);
    HasText companyName();
    HasText actualAddress();
    HasText legalAddress();

    HasValidable companyNameValidator();
    HasValidable actualAddressValidator();
    HasValidable legalAddressValidator();

    HasText webSite();
    HasText comment();
    HasValue<CompanyGroup> companyGroup();
    HasValue<CompanyCategory> companyCategory();

    HasWidgets phonesContainer();
    HasWidgets emailsContainer();
}
