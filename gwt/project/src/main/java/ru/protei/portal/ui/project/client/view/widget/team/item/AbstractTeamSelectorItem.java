package ru.protei.portal.ui.project.client.view.widget.team.item;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.ui.project.client.view.widget.team.AbstractTeamSelector;

import java.util.List;

public interface AbstractTeamSelectorItem extends IsWidget {

    void setActivity(AbstractTeamSelector activity);

    void setAvailableRoles(List<En_DevUnitPersonRoleType> availableRoles);

    void setModel(TeamSelectorItemModel model);

    List<En_DevUnitPersonRoleType> getAvailableRoles();

    HasEnabled roleEnabled();

    HasEnabled membersEnabled();

    void setRoleMandatory(boolean isMandatory);

    HasValue<En_DevUnitPersonRoleType> role();
}
