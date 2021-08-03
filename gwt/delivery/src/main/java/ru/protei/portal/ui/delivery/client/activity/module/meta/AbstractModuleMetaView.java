package ru.protei.portal.ui.delivery.client.activity.module.meta;

import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;

public interface AbstractModuleMetaView {
    void setActivity(AbstractModuleMetaActivity activity);

    HasValue<CaseState> state();

    void setManager(String value);

    void setCustomerCompany(String value);

    HasValue<PersonShortView> hwManager();

    HasValue<PersonShortView> qcManager();
}
