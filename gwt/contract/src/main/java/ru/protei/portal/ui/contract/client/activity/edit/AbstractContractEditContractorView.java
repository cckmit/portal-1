package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

public interface AbstractContractEditContractorView {

    void setOrganization(String organization);

    HasValue<EntityOption> organization();

    HasValue<Contractor> contractor();

    HasEnabled contractorEnabled();
}
