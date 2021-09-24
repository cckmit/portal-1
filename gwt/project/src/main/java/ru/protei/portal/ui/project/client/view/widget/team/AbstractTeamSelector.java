package ru.protei.portal.ui.project.client.view.widget.team;

import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.ui.project.client.view.widget.team.item.TeamSelectorItemModel;

public interface AbstractTeamSelector {

    void onModelChanged(TeamSelectorItemModel model);

    void onRoleChanged(TeamSelectorItemModel model, En_PersonRoleType previous, En_PersonRoleType actual);
}
