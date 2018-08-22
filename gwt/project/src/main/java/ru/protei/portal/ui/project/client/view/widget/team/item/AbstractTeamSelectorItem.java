package ru.protei.portal.ui.project.client.view.widget.team.item;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.ui.project.client.view.widget.team.AbstractTeamSelector;

import java.util.List;

public interface AbstractTeamSelectorItem extends IsWidget, HasEnabled {

    void setActivity(AbstractTeamSelector activity);

    void setAvailableRoles(List<En_DevUnitPersonRoleType> availableRoles);

    void setModel(TeamSelectorItemModel model);

    List<En_DevUnitPersonRoleType> getAvailableRoles();
}