package ru.protei.portal.ui.project.client.view.widget.team;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.project.client.view.widget.team.item.AbstractTeamSelectorItem;
import ru.protei.portal.ui.project.client.view.widget.team.item.TeamSelectorItemModel;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;

public class TeamSelector extends Composite implements AbstractTeamSelector, HasEnabled, HasValue<Set<PersonProjectMemberView>> {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setValue(Set<PersonProjectMemberView> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<PersonProjectMemberView> value, boolean fireEvents) {
        root.clear();
        model.clear();
        modelToView.clear();
        TeamSelectorItemModel.toModel(value).forEach(this::addNewItem);
        addNewEmptyItemIfNeeded();
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public Set<PersonProjectMemberView> getValue() {
        return TeamSelectorItemModel.fromModel(model);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<PersonProjectMemberView>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        if (isEnabled) {
            root.removeStyleName("disabled");
        } else {
            root.addStyleName("disabled");
        }
        modelToView.values().forEach(itemView -> {
            itemView.roleEnabled().setEnabled(enabled && !En_PersonRoleType.HEAD_MANAGER.equals(itemView.role().getValue()));
            itemView.membersEnabled().setEnabled(enabled);
        });
    }

    @Override
    public void onModelChanged(TeamSelectorItemModel itemModel) {
        if (itemModel == null) {
            return;
        }
        if (!itemModel.allowEmptyMembers && itemModel.members.size() == 0 && !En_PersonRoleType.HEAD_MANAGER.equals(itemModel.role)) {
            removeItem(itemModel);
            onRoleChanged(itemModel, itemModel.role, null);
        }
        if (itemModel.members.size() > 0) {
            model.add(itemModel);
            if (itemModel.allowEmptyMembers) {
                itemModel.allowEmptyMembers = false;
            }
        }
        addNewEmptyItemIfNeeded();
        fireValueChanged();
    }

    @Override
    public void onRoleChanged(TeamSelectorItemModel target, En_PersonRoleType previous, En_PersonRoleType actual) {
        for (Map.Entry<TeamSelectorItemModel, AbstractTeamSelectorItem> entry : modelToView.entrySet()) {
            TeamSelectorItemModel itemModel = entry.getKey();
            AbstractTeamSelectorItem itemView = entry.getValue();
            if (itemModel.equals(target)) {
                continue;
            }
            List<En_PersonRoleType> availableRoles = itemView.getAvailableRoles();
            if (actual != null) {
                availableRoles.remove(actual);
            }
            if (previous != null && !availableRoles.contains(previous)) {
                availableRoles.add(previous);
            }
            itemView.setAvailableRoles(availableRoles);
        }
    }

    private void fireValueChanged() {
        ValueChangeEvent.fire(this, getValue());
    }

    private void addNewItem(TeamSelectorItemModel itemModel) {
        List<En_PersonRoleType> availableRoles = getAvailableRoles();
        if (availableRoles.size() == 0) {
            return;
        }
        if (itemModel.role == null || !availableRoles.contains(itemModel.role)) {
            itemModel.role = availableRoles.get(0);
        }
        if (itemModel.members == null) {
            itemModel.members = new HashSet<>();
        }
        AbstractTeamSelectorItem itemView = createItemView();
        itemView.setAvailableRoles(availableRoles);
        itemView.setModel(itemModel);
        itemView.membersEnabled().setEnabled(isEnabled);
        itemView.roleEnabled().setEnabled(isEnabled && !En_PersonRoleType.HEAD_MANAGER.equals(itemModel.role));
        itemView.setRoleMandatory(En_PersonRoleType.HEAD_MANAGER.equals(itemModel.role));
        model.add(itemModel);
        modelToView.put(itemModel, itemView);
        root.add(itemView.asWidget());
    }

    private void addNewEmptyItemIfNeeded() {
        if (!isEmptyItemExists()) {
            addNewItem(new TeamSelectorItemModel(null, true));
        }
    }

    private void removeItem(TeamSelectorItemModel itemModel) {
        AbstractTeamSelectorItem itemView = modelToView.get(itemModel);
        root.remove(itemView);
        model.remove(itemModel);
        modelToView.remove(itemModel);
    }

    private List<En_PersonRoleType> getAvailableRoles() {
        List<En_PersonRoleType> roles = listOf(En_PersonRoleType.getProjectRoles());
        model.forEach(itemModel -> roles.remove(itemModel.role));
        return roles;
    }

    private AbstractTeamSelectorItem createItemView() {
        AbstractTeamSelectorItem itemView = itemFactory.get();
        itemView.setActivity(this);
        return itemView;
    }

    private boolean isEmptyItemExists() {
        for (TeamSelectorItemModel item : model) {
            if (item.allowEmptyMembers) {
                return true;
            }
        }
        return false;
    }

    @UiField
    HTMLPanel root;

    @Inject
    Provider<AbstractTeamSelectorItem> itemFactory;

    private Set<TeamSelectorItemModel> model = new HashSet<>();
    private Map<TeamSelectorItemModel, AbstractTeamSelectorItem> modelToView = new HashMap<>();
    private boolean isEnabled = true;

    interface TeamSelectorUiBinder extends UiBinder<HTMLPanel, TeamSelector> {}
    private static TeamSelectorUiBinder ourUiBinder = GWT.create(TeamSelectorUiBinder.class);
}
