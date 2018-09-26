package ru.protei.portal.ui.crm.client.activity.profile;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;

import java.util.List;

/**
 * Абстракция вида превью контакта
 */
public interface AbstractProfilePageView extends IsWidget {

    void setActivity( AbstractProfilePageActivity activity );

    HasValue<List<Subscription>> companySubscription();

    void setName( String name );

    void setCompany( String value );

    HasVisibility saveButtonVisibility();

    void setIcon( String iconSrc );
}
