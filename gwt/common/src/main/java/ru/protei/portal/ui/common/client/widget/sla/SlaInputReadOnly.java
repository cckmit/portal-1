package ru.protei.portal.ui.common.client.widget.sla;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.sla.items.SlaRowItemReadOnly;

import java.util.*;
import java.util.stream.Collectors;

public class SlaInputReadOnly extends Composite implements HasValue<List<ProjectSla>> {
    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initView();
    }

    @Override
    public List<ProjectSla> getValue() {
        return collectSla();
    }

    @Override
    public void setValue(List<ProjectSla> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(List<ProjectSla> value, boolean fireEvents) {
        clearView();

        if (CollectionUtils.isNotEmpty(value)) {
            value.forEach(projectSla -> importanceToItemMap.get(En_ImportanceLevel.find(projectSla.getImportanceLevelId())).setValue(projectSla));
        }

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ProjectSla>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void initView() {
        for (En_ImportanceLevel importance : En_ImportanceLevel.values()) {
            SlaRowItemReadOnly item = slaRowItemReadOnlyProvider.get();
            item.setImportance(importanceLang.getImportanceName(importance));
            importanceToItemMap.put(importance, item);
            root.add(item.asWidget());
        }
    }

    private void clearView() {
        importanceToItemMap.values().forEach(SlaRowItemReadOnly::clear);
    }

    private List<ProjectSla> collectSla() {
        return importanceToItemMap.values().stream().map(SlaRowItemReadOnly::getValue).collect(Collectors.toList());
    }

    @UiField
    HTMLPanel root;

    @Inject
    private Provider<SlaRowItemReadOnly> slaRowItemReadOnlyProvider;

    @Inject
    private En_CaseImportanceLang importanceLang;

    private Map<En_ImportanceLevel, SlaRowItemReadOnly> importanceToItemMap = new HashMap<>();

    interface SlaInputReadOnlyUiBinder extends UiBinder<HTMLPanel, SlaInputReadOnly> {}
    private static SlaInputReadOnlyUiBinder ourUiBinder = GWT.create(SlaInputReadOnlyUiBinder.class);
}