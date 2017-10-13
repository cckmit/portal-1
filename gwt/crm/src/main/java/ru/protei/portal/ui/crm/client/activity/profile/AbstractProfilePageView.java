package ru.protei.portal.ui.crm.client.activity.profile;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CompanySubscription;

import java.util.List;

/**
 * Абстракция вида превью контакта
 */
public interface AbstractProfilePageView extends IsWidget {

    void setActivity( AbstractProfilePageActivity activity );

    HasValue<List<CompanySubscription>> companySubscription();

    void setName( String name );

    void setRoles( String value );
}
