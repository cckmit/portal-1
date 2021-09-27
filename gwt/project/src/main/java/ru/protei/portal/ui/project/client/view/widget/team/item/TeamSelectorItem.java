package ru.protei.portal.ui.project.client.view.widget.team.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.project.client.view.widget.selector.ProjectRoleFormSelector;
import ru.protei.portal.ui.project.client.view.widget.team.AbstractTeamSelector;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamSelectorItem extends Composite implements AbstractTeamSelectorItem {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractTeamSelector teamSelector) {
        this.teamSelector = teamSelector;
    }

    @Override
    public void setAvailableRoles(List<En_PersonRoleType> availableRoles) {
        this.availableRoles = availableRoles;
        if (availableRoles == null || availableRoles.size() == 0) {
            return;
        }
        availableRoles.sort(Comparator.comparingInt(En_PersonRoleType::getId));
        role.fillOptions(availableRoles);
    }

    @Override
    public void setModel(TeamSelectorItemModel model) {
        if (model == null) {
            return;
        }
        this.model = model;
        this.role.setValue(model.role);
        members.setValue(model.members, false);
        ensureDebugIds();
        fireRoleChanged(null, model.role);
    }

    @Override
    public List<En_PersonRoleType> getAvailableRoles() {
        return availableRoles;
    }

    @Override
    public HasEnabled roleEnabled() {
        return role;
    }

    @Override
    public HasEnabled membersEnabled() {
        return members;
    }

    @Override
    public void setRoleMandatory(boolean isMandatory) {
        role.setMandatory(isMandatory);
    }

    @Override
    public HasValue<En_PersonRoleType> role() {
        return role;
    }

    @UiHandler("role")
    public void onRoleChanged(ValueChangeEvent<En_PersonRoleType> event) {
        En_PersonRoleType roleValue = event.getValue();
        En_PersonRoleType previousRoleValue = model.role;
        if (roleValue == null) {
            return;
        }
        if (roleValue.equals(previousRoleValue)) {
            return;
        }
        model.role = roleValue;
        fireRoleChanged(previousRoleValue, roleValue);
        fireModelChanged();
    }

    @UiHandler("members")
    public void onMembersChanged(ValueChangeEvent<Set<PersonShortView>> event) {
        model.members = event.getValue() == null ? new HashSet<>() : event.getValue();
        fireModelChanged();
    }

    private void fireModelChanged() {
        if (teamSelector != null && model != null) {
            teamSelector.onModelChanged(model);
        }
    }

    private void fireRoleChanged(En_PersonRoleType previous, En_PersonRoleType actual) {
        if (teamSelector != null) {
            teamSelector.onRoleChanged(model, previous, actual);
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        root.ensureDebugId(DebugIds.PROJECT.TEAM_MEMBER_ROLE + model.role.toString().replaceAll("_", "-").toLowerCase());
        role.ensureDebugId(DebugIds.PROJECT.TEAM_MEMBER_ROLE_SELECTOR);
        members.ensureDebugId(DebugIds.PROJECT.TEAM_MEMBER_SELECTOR);
        members.setAddEnsureDebugId(DebugIds.PROJECT.TEAM_MEMBER_ADD_BUTTON);
        members.setClearEnsureDebugId(DebugIds.PROJECT.TEAM_MEMBER_CLEAR_BUTTON);
        members.setItemContainerEnsureDebugId(DebugIds.PROJECT.TEAM_MEMBER_ITEM_CONTAINER);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    ProjectRoleFormSelector role;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector members;

    private TeamSelectorItemModel model = null;
    private AbstractTeamSelector teamSelector = null;
    private List<En_PersonRoleType> availableRoles = null;

    interface TeamSelectorItemUiBinder extends UiBinder<HTMLPanel, TeamSelectorItem> {}
    private static TeamSelectorItemUiBinder ourUiBinder = GWT.create(TeamSelectorItemUiBinder.class);
}
