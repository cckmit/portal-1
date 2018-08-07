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
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.project.client.view.widget.team.item.AbstractTeamSelectorItem;
import ru.protei.portal.ui.project.client.view.widget.team.item.TeamSelectorItemModel;

import java.util.*;

public class TeamSelector extends Composite implements
        AbstractTeamSelector,
        HasValue<Set<PersonProjectMemberView>>, HasEnabled,
        ValueChangeHandler<Set<PersonProjectMemberView>> {

    public TeamSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setValue(Set<PersonProjectMemberView> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<PersonProjectMemberView> value, boolean fireEvents) {
        model.clear();
        if (value != null) {
            value.forEach(ppm -> addItemToModel(ppm.getRole(), ppm));
        }
        drawItems();
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public Set<PersonProjectMemberView> getValue() {
        Set<PersonProjectMemberView> values = new HashSet<>();
        model.forEach(tsim -> tsim.members.forEach(psv -> {
            values.add(PersonProjectMemberView.fromPersonShortView(psv, tsim.role));
        }));
        return values;
    }

    @Override
    public void onValueChange(ValueChangeEvent<Set<PersonProjectMemberView>> event) {}

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

    }

    @Override
    public void onMemberAdded(En_DevUnitPersonRoleType role, PersonShortView member) {
        addItemToModel(role, member);
        drawItems();
    }

    @Override
    public void onMemberRemoved(En_DevUnitPersonRoleType role, PersonShortView member) {
        removeItemFromModel(role, member);
        drawItems();
    }

    private void drawItems() {
        root.clear();
        modelToView.clear();
        model.forEach(itemModel -> {
            AbstractTeamSelectorItem itemView = createItemView();
            itemView.setModel(itemModel);
            root.add(itemView.asWidget());
            modelToView.put(itemModel, itemView);
        });
    }

    private void addItemToModel(En_DevUnitPersonRoleType role, PersonShortView member) {
        TeamSelectorItemModel itemModel = getModelItemWithRole(role);
        if (itemModel == null) {
            itemModel = new TeamSelectorItemModel(root, role);
            itemModel.members.add(member);
            model.add(itemModel);
        } else {
            itemModel.members.add(member);
        }
    }

    private void removeItemFromModel(En_DevUnitPersonRoleType role, PersonShortView member) {
        TeamSelectorItemModel itemModel = getModelItemWithRole(role);
        if (itemModel == null) {
            return;
        }
        itemModel.members.remove(member);
        if (itemModel.members.size() == 0) {
            model.remove(itemModel);
        }
    }

    private AbstractTeamSelectorItem createItemView() {
        AbstractTeamSelectorItem itemView = itemFactory.get();
        itemView.setActivity(this);
        return itemView;
    }

    private TeamSelectorItemModel getModelItemWithRole(En_DevUnitPersonRoleType role) {
        for (TeamSelectorItemModel item : model) {
            if (Objects.equals(item.role, role)) {
                return item;
            }
        }
        return null;
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
