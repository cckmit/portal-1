package ru.protei.portal.ui.project.client.view.widget.team;

import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.view.PersonShortView;

public interface AbstractTeamSelector {

    void onMemberAdded(En_DevUnitPersonRoleType role, PersonShortView member);

    void onMemberRemoved(En_DevUnitPersonRoleType role, PersonShortView member);
}
