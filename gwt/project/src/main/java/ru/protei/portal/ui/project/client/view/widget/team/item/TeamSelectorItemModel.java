package ru.protei.portal.ui.project.client.view.widget.team.item;

import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;

public class TeamSelectorItemModel {

    public En_PersonRoleType role;
    public Set<PersonShortView> members;
    public boolean allowEmptyMembers;
    public boolean singleMember;

    public TeamSelectorItemModel(En_PersonRoleType role) {
        this(role, new HashSet<>());
    }

    public TeamSelectorItemModel(En_PersonRoleType role, boolean allowEmptyMembers) {
        this(role, new HashSet<>(), allowEmptyMembers);
    }

    public TeamSelectorItemModel(En_PersonRoleType role, Set<PersonShortView> members) {
        this(role, members, false);
    }

    public TeamSelectorItemModel(En_PersonRoleType role, Set<PersonShortView> members, boolean allowEmptyMembers) {
        this.role = role;
        this.members = members;
        this.allowEmptyMembers = allowEmptyMembers;
        this.singleMember = false;
    }

    public static List<TeamSelectorItemModel> toModel(Collection<PersonProjectMemberView> value) {
        List<TeamSelectorItemModel> model = new ArrayList<>();
        if (value != null && value.size() > 0) {
            value.forEach(ppm -> {
                TeamSelectorItemModel itemModel = getModelItemWithRole(model, ppm.getRole());
                if (itemModel == null) {
                    itemModel = new TeamSelectorItemModel(ppm.getRole());
                    itemModel.members.add(ppm);
                    model.add(itemModel);
                } else {
                    itemModel.members.add(ppm);
                }
            });
            model.sort(Comparator.comparingInt(m -> m.role.getId()));
        }
        return model;
    }

    public static Set<PersonProjectMemberView> fromModel(Collection<TeamSelectorItemModel> model) {
        Set<PersonProjectMemberView> values = new HashSet<>();
        model.forEach(tsim -> tsim.members.forEach(psv -> {
            values.add(new PersonProjectMemberView(psv, tsim.role));
        }));
        return values;
    }

    private static TeamSelectorItemModel getModelItemWithRole(Collection<TeamSelectorItemModel> model, En_PersonRoleType role) {
        for (TeamSelectorItemModel item : model) {
            if (Objects.equals(item.role, role)) {
                return item;
            }
        }
        return null;
    }
}
