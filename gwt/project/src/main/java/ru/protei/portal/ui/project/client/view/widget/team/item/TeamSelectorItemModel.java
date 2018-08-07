package ru.protei.portal.ui.project.client.view.widget.team.item;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.HashSet;
import java.util.Set;

public class TeamSelectorItemModel {

    public HasWidgets parent;
    public En_DevUnitPersonRoleType role;
    public Set<PersonShortView> members;

    public TeamSelectorItemModel(HasWidgets parent, En_DevUnitPersonRoleType role) {
        this(parent, role, new HashSet<>());
    }

    public TeamSelectorItemModel(HasWidgets parent, En_DevUnitPersonRoleType role, Set<PersonShortView> members) {
        this.parent = parent;
        this.role = role;
        this.members = members;
    }
}
