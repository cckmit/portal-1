package ru.protei.portal.ui.delivery.client.widget.cardbatch.contractors.item;

import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;

public class ContractorsSelectorItemModel {

    public En_PersonRoleType role;
    public Set<PersonShortView> members;
    public boolean allowEmptyMembers;
    public boolean singleMember;

    public ContractorsSelectorItemModel(En_PersonRoleType role) {
        this(role, new HashSet<>());
    }

    public ContractorsSelectorItemModel(En_PersonRoleType role, boolean allowEmptyMembers) {
        this(role, new HashSet<>(), allowEmptyMembers);
    }

    public ContractorsSelectorItemModel(En_PersonRoleType role, Set<PersonShortView> members) {
        this(role, members, false);
    }

    public ContractorsSelectorItemModel(En_PersonRoleType role, Set<PersonShortView> members, boolean allowEmptyMembers) {
        this.role = role;
        this.members = members;
        this.allowEmptyMembers = allowEmptyMembers;
        this.singleMember = false;
    }

    public static List<ContractorsSelectorItemModel> toModel(Collection<PersonProjectMemberView> value) {
        List<ContractorsSelectorItemModel> model = new ArrayList<>();
        if (value != null && value.size() > 0) {
            value.forEach(ppm -> {
                ContractorsSelectorItemModel itemModel = getModelItemWithRole(model, ppm.getRole());
                if (itemModel == null) {
                    itemModel = new ContractorsSelectorItemModel(ppm.getRole());
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

    public static Set<PersonProjectMemberView> fromModel(Collection<ContractorsSelectorItemModel> model) {
        Set<PersonProjectMemberView> values = new HashSet<>();
        model.forEach(tsim -> tsim.members.forEach(psv -> {
            values.add(new PersonProjectMemberView(psv, tsim.role));
        }));
        return values;
    }

    private static ContractorsSelectorItemModel getModelItemWithRole(Collection<ContractorsSelectorItemModel> model, En_PersonRoleType role) {
        for (ContractorsSelectorItemModel item : model) {
            if (Objects.equals(item.role, role)) {
                return item;
            }
        }
        return null;
    }
}
