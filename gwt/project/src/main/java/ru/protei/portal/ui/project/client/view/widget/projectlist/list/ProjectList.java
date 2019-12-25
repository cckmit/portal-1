package ru.protei.portal.ui.project.client.view.widget.projectlist.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.project.client.view.widget.projectlist.item.ProjectItem;

import java.util.*;
import java.util.stream.Collectors;

public class ProjectList
        extends Composite
        implements HasValue<Project>, ValueChangeHandler<Boolean> {

    public ProjectList() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public Project getValue() {
        return selected;
    }

    @Override
    public void setValue(Project value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Project value, boolean fireEvents) {
        selected = value;
        for (Map.Entry<Project, ProjectItem > entry : itemToViewModel.entrySet()) {
            entry.getValue().setValue(selected != null && selected.equals(entry.getKey()));
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, selected);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Project> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        Project value = itemViewToModel.get(event.getSource());
        if (value == null && !itemViewToModel.containsKey(event.getSource())) {
            return;
        }

        setValue(value, true);
    }

    public void addItems(List<Project> list) {
        list.forEach(this::addItem);
    }

    public void clearItems() {
        container.clear();
        itemViewToModel.clear();
        itemToViewModel.clear();
        selected = null;
    }

    private void addItem(Project value) {
        ProjectItem itemView = itemFactory.get();
        itemView.setCreated(value.getCreated() == null ? "" : DateFormatter.formatDateTime(value.getCreated()));
        itemView.setName(value.getName());
        itemView.setProducts(value.getProducts() == null ? "" : value.getProducts().stream().map(product -> product.getName()).collect(Collectors.joining(", ")));
        itemView.setCustomerType(customerTypeLang.getName(value.getCustomerType()));
        itemView.setManagers(makeManagers(value));
        itemView.addValueChangeHandler(this);
        itemView.setValue(selected != null && selected.equals(value));

        itemViewToModel.put(itemView, value);
        itemToViewModel.put(value, itemView);
        container.add(itemView.asWidget());
    }

    private String makeManagers(Project value) {
        StringBuilder content = new StringBuilder();

        List< PersonProjectMemberView > team = value.getTeam();
        if (team != null) {
            PersonProjectMemberView leader = team.stream()
                    .filter(ppm -> En_DevUnitPersonRoleType.HEAD_MANAGER.equals(ppm.getRole()))
                    .findFirst()
                    .orElse(null);
            if (leader != null) {
                content.append(leader.getName());
                if (team.size() - 1 > 0) {
                    content.append(" +")
                            .append(team.size() - 1)
                            .append(" ")
                            .append(lang.membersCount());
                }
            } else if (team.size() > 0) {
                content.append(team.size())
                        .append(" ")
                        .append(lang.membersCount());
            }
        }
        return content.toString();
    }

    @UiField
    FlowPanel container;

    @Inject
    Provider<ProjectItem> itemFactory;

    Project selected;

    Map<ProjectItem, Project> itemViewToModel = new HashMap<>();
    Map<Project, ProjectItem> itemToViewModel = new HashMap<>();

    @Inject
    En_CustomerTypeLang customerTypeLang;

    @Inject
    @UiField
    Lang lang;

    private static ProjectListUiBinder ourUiBinder = GWT.create(ProjectListUiBinder.class);
    interface ProjectListUiBinder extends UiBinder<HTMLPanel, ProjectList> {}
}