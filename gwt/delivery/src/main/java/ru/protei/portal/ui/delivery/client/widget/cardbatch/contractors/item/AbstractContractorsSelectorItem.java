package ru.protei.portal.ui.delivery.client.widget.cardbatch.contractors.item;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.ui.delivery.client.widget.cardbatch.contractors.AbstractContractorsSelector;

import java.util.List;

public interface AbstractContractorsSelectorItem extends IsWidget {

    void setActivity(AbstractContractorsSelector activity);

    void setAvailableRoles(List<En_PersonRoleType> availableRoles);

    void setModel(ContractorsSelectorItemModel model);

    List<En_PersonRoleType> getAvailableRoles();

    HasEnabled roleEnabled();

    HasEnabled membersEnabled();

    void setRoleMandatory(boolean isMandatory);

    HasValue<En_PersonRoleType> role();
}
