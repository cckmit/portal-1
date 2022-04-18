package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.ent.CommonManager;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
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

    HasText webSite();
    HasText comment();

    HasText companyNameErrorLabel();

    HasVisibility companyNameErrorLabelVisibility();

    HasValue<EntityOption> parentCompany();

    HasValue<En_CompanyCategory> companyCategory();

    HasValue<List<CompanySubscription> > companySubscriptions();
    HasValidable companySubscriptionsValidator();

    HasWidgets phonesContainer();
    HasWidgets emailsContainer();

    HasWidgets employeeRegistrationEmailsContainer();

    HasVisibility employeeRegistrationEmailsContainerVisibility();

    HasWidgets probationEmailsContainer();

    HasVisibility probationEmailsContainerVisibility();

    HasWidgets tableContainer();
    HasWidgets siteFolderContainer();

    void setParentCompanyFilter(Selector.SelectorFilter<EntityOption> companyFilter );
    void setParentCompanyEnabled( boolean isEnabled );
    void setCompanyIdToSubscriptionsList(Long companyId);

    HasValue<Boolean> autoOpenIssues();

    HasValue<List<CommonManager> > commonManagerList();
}
