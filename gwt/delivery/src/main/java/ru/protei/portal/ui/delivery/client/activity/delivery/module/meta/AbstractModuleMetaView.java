package ru.protei.portal.ui.delivery.client.activity.delivery.module.meta;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;

public interface AbstractModuleMetaView extends IsWidget {
    void setActivity(AbstractModuleMetaActivity activity);

    HasValue<CaseState> state();

    void setManager(String value);

    void setCustomerCompany(String value);

    HasValue<PersonShortView> hwManager();

    HasValue<PersonShortView> qcManager();

    HasValue<Date> buildDate();

    HasValue<Date> departureDate();
    
    HasValue<String> rfidLabel();

    void setBuildDateValid(boolean isValid);

    void setDepartureDateValid(boolean isValid);

    boolean isBuildDateEmpty();

    boolean isDepartureDateEmpty();

    void setAllowChangingState(boolean isAllow);

    HasEnabled stateEnabled();

    HasEnabled hwManagerEnabled();

    HasEnabled qcManagerEnabled();

    HasEnabled buildDateEnabled();

    HasEnabled departureDateEnabled();
}
