package ru.protei.portal.ui.project.client.view.widget.team.item;

import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.HashSet;
import java.util.Set;

public class TeamSelectorItemModel {

    public En_DevUnitPersonRoleType role;
    public Set<PersonShortView> members;
    public boolean allowEmptyMembers;
    public boolean singleMember;

    public TeamSelectorItemModel(En_DevUnitPersonRoleType role) {
        this(role, new HashSet<>());
    }

    public TeamSelectorItemModel(En_DevUnitPersonRoleType role, boolean allowEmptyMembers) {
        this(role, new HashSet<>(), allowEmptyMembers);
    }

    public TeamSelectorItemModel(En_DevUnitPersonRoleType role, Set<PersonShortView> members) {
        this(role, members, false);
    }

    public TeamSelectorItemModel(En_DevUnitPersonRoleType role, Set<PersonShortView> members, boolean allowEmptyMembers) {
        this.role = role;
        this.members = members;
        this.allowEmptyMembers = allowEmptyMembers;
        this.singleMember = false;
    }
}
