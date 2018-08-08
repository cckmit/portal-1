package ru.protei.portal.ui.project.client.view.widget.team.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.project.client.view.widget.selector.ProjectRoleButtonSelector;
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
    public void setAvailableRoles(List<En_DevUnitPersonRoleType> availableRoles) {
        this.availableRoles = availableRoles;
        if (availableRoles == null || availableRoles.size() == 0) {
            return;
        }
        availableRoles.sort(Comparator.comparingInt(En_DevUnitPersonRoleType::getId));
        role.fillOptions(availableRoles);
    }

    @Override
    public void setModel(TeamSelectorItemModel model) {
        if (model == null) {
            return;
        }
        this.model = model;
        role.setValue(model.role);
        members.setValue(model.members, false);
        fireRoleChanged(null, model.role);
        applySingleMember();
    }

    @Override
    public List<En_DevUnitPersonRoleType> getAvailableRoles() {
        return availableRoles;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        role.setEnabled(enabled);
        members.setEnabled(enabled);
    }

    @UiHandler("role")
    public void onRoleChanged(ValueChangeEvent<En_DevUnitPersonRoleType> event) {
        En_DevUnitPersonRoleType roleValue = event.getValue();
        if (roleValue == null) {
            return;
        }
        if (roleValue.equals(model.role)) {
            return;
        }
        fireRoleChanged(model.role, roleValue);
        model.role = roleValue;
        fireModelChanged();
        applySingleMember();
    }

    @UiHandler("members")
    public void onMembersChanged(ValueChangeEvent<Set<PersonShortView>> event) {
        model.members = event.getValue() == null ? new HashSet<>() : event.getValue();
        if (model.members.size() == 0) {
            members.hidePopup();
        }
        if (!applySingleMember()) {
            fireModelChanged();
        }
    }

    private boolean applySingleMember() {
        model.singleMember = En_DevUnitPersonRoleType.HEAD_MANAGER.equals(model.role);
        if (model.singleMember && model.members.size() > 1) {
            Set<PersonShortView> value = new HashSet<>();
            value.add(model.members.stream().findFirst().get());
            members.setValue(value, true);
            return true;
        }
        return false;
    }

    private void fireModelChanged() {
        if (teamSelector != null && model != null) {
            teamSelector.onModelChanged(model);
        }
    }

    private void fireRoleChanged(En_DevUnitPersonRoleType previous, En_DevUnitPersonRoleType actual) {
        if (teamSelector != null) {
            teamSelector.onRoleChanged(model, previous, actual);
        }
    }

    @Inject
    @UiField(provided = true)
    ProjectRoleButtonSelector role;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector members;

    private TeamSelectorItemModel model = null;
    private AbstractTeamSelector teamSelector = null;
    private List<En_DevUnitPersonRoleType> availableRoles = null;
    private boolean isEnabled = true;

    interface TeamSelectorItemUiBinder extends UiBinder<HTMLPanel, TeamSelectorItem> {}
    private static TeamSelectorItemUiBinder ourUiBinder = GWT.create(TeamSelectorItemUiBinder.class);
}
