package ru.protei.portal.ui.common.client.widget.projectlist.list;

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
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.projectlist.item.ProjectItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectList
        extends Composite
        implements HasValue<ProjectInfo>, ValueChangeHandler<Boolean> {

    public ProjectList() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public ProjectInfo getValue() {
        return selected;
    }

    @Override
    public void setValue(ProjectInfo value) {
        setValue(value, false);
    }

    @Override
    public void setValue(ProjectInfo value, boolean fireEvents) {
        selected = value;
        for (Map.Entry<ProjectInfo, ProjectItem > entry : itemToViewModel.entrySet()) {
            entry.getValue().setValue(selected != null && selected.equals(entry.getKey()));
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, selected);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ProjectInfo> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        ProjectInfo value = itemViewToModel.get(event.getSource());
        if (value == null && !itemViewToModel.containsKey(event.getSource())) {
            return;
        }

        setValue(value, true);
    }

    public void addItems(List<ProjectInfo> list) {
        list.forEach(this::addItem);
    }

    public void clearItems() {
        container.clear();
        itemViewToModel.clear();
        itemToViewModel.clear();
        selected = null;
    }

    private void addItem(ProjectInfo value) {
        ProjectItem itemView = itemFactory.get();
        itemView.setCreated(value.getCreated() == null ? "" : DateFormatter.formatDateTime(value.getCreated()));
        itemView.setId(String.valueOf(value.getId()));
        itemView.setName(value.getName());
        itemView.setProducts(value.getProducts() == null ? "" : value.getProducts().stream().map(product -> product.getName()).collect(Collectors.joining(", ")));
        itemView.setCustomerType(customerTypeLang.getName(value.getCustomerType()));
        itemView.setManagers(value.getManager() == null ? "" : value.getManager().getName());
        itemView.addValueChangeHandler(this);
        itemView.setValue(selected != null && selected.equals(value));

        itemViewToModel.put(itemView, value);
        itemToViewModel.put(value, itemView);
        container.add(itemView.asWidget());
    }

    @UiField
    FlowPanel container;

    @Inject
    Provider<ProjectItem> itemFactory;

    ProjectInfo selected;

    Map<ProjectItem, ProjectInfo> itemViewToModel = new HashMap<>();
    Map<ProjectInfo, ProjectItem> itemToViewModel = new HashMap<>();

    @Inject
    En_CustomerTypeLang customerTypeLang;

    @Inject
    @UiField
    Lang lang;

    private static ProjectListUiBinder ourUiBinder = GWT.create(ProjectListUiBinder.class);
    interface ProjectListUiBinder extends UiBinder<HTMLPanel, ProjectList> {}
}