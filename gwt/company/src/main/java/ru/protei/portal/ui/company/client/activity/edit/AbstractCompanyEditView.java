package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.List;

/**
 * Представление создания и редактирования компании
 */
public interface AbstractCompanyEditView extends IsWidget {

    void setActivity( AbstractCompanyEditActivity activity );

    void setCompanyNameStatus(NameStatus status);
    HasValue<String> companyName();
    HasValue<String> actualAddress();
    HasValue<String> legalAddress();

    HasValidable companyNameValidator();
    HasValidable actualAddressValidator();
    HasValidable legalAddressValidator();

    HasText webSite();
    HasText comment();
    HasValue<EntityOption> companyGroup();
    HasValue<EntityOption> companyCategory();

    HasValue<List<Subscription> > companySubscriptions();
    HasValidable companySubscriptionsValidator();

    HasWidgets phonesContainer();
    HasWidgets emailsContainer();

    HasWidgets tableContainer();
}
