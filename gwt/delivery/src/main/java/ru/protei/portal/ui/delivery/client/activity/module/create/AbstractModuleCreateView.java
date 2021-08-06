package ru.protei.portal.ui.delivery.client.activity.module.create;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;

public interface AbstractModuleCreateView extends IsWidget {

    void setActivity(AbstractModuleCreateActivity activity);

    HasEnabled saveEnabled();

    void setSerialNumber(String serialNumber);

    HasValue<String> name();

    HasValue<String> description();

    HasValue<CaseState> state();

    HasValue<Date> departureDate();

    void setManager(String value);

    HasValue<PersonShortView> hwManager();

    HasValue<PersonShortView> qcManager();

    void setCustomerCompany(String value);

    HasValue<Date> buildDate();
}
