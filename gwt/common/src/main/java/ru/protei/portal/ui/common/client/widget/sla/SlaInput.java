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
import ru.protei.portal.ui.common.client.widget.sla.items.SlaRowItem;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SlaInput extends Composite implements HasValue<List<ProjectSla>>, HasValidable {
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
        clearItems();

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

    @Override
    public void setValid(boolean isValid) {
        for (SlaRowItem item : importanceToItemMap.values()) {
            item.setValid(isValid);
        }
    }

    @Override
    public boolean isValid() {
        return importanceToItemMap.values().stream().allMatch(SlaRowItem::isValid);
    }

    public void setEnsureDebugId(String debugId) {
        for (SlaRowItem item : importanceToItemMap.values()) {
            item.setEnsureDebugId(debugId);
        }
    }

    private void initView() {
        for (En_ImportanceLevel importance : En_ImportanceLevel.values(true)) {
            SlaRowItem item = slaRowItemProvider.get();
            item.setImportance(importance);
            importanceToItemMap.put(importance, item);
            itemsContainer.add(item.asWidget());
        }
    }

    private void clearItems() {
        importanceToItemMap.values().forEach(SlaRowItem::clear);
    }

    private List<ProjectSla> collectSla() {
        return importanceToItemMap.values()
                .stream()
                .map(SlaRowItem::getValue)
                .collect(Collectors.toList());
    }

    @UiField
    HTMLPanel itemsContainer;

    @Inject
    private Provider<SlaRowItem> slaRowItemProvider;

    private Map<En_ImportanceLevel, SlaRowItem> importanceToItemMap = new HashMap<>();

    interface SlaInputUiBinder extends UiBinder<HTMLPanel, SlaInput> {
    }

    private static SlaInputUiBinder ourUiBinder = GWT.create(SlaInputUiBinder.class);
}