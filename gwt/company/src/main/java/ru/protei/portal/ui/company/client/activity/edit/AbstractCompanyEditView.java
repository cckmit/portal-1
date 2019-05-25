package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
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

    HasText webSite();
    HasText comment();
    HasValue<EntityOption> parentCompany();

    HasValue<EntityOption> companyCategory();

    HasValue<List<Subscription> > companySubscriptions();
    HasValidable companySubscriptionsValidator();

    HasWidgets phonesContainer();
    HasWidgets emailsContainer();

    HasWidgets tableContainer();
    HasWidgets siteFolderContainer();
    HasVisibility tableContainerVisibility();
    HasVisibility siteFolderContainerVisibility();

    void setParentCompanyFilter( Selector.SelectorFilter<EntityOption> companyFilter );

    void setParentCompanyEnabled( boolean isEnabled );

}
