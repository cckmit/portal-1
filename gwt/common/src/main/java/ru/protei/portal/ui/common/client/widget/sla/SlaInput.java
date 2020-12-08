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
import ru.protei.portal.ui.common.client.widget.sla.items.SlaRowItem;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class SlaInput extends Composite implements HasValue<List<ProjectSla>>, HasValidable {
    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
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
        importanceToItemMap.clear();
        itemsContainer.clear();

        emptyIfNull(value).forEach(projectSla -> {
            En_ImportanceLevel importanceLevel = projectSla.getImportanceLevel();

            SlaRowItem item = slaRowItemProvider.get();
            item.setValue(projectSla);
            importanceToItemMap.put(importanceLevel, item);
            itemsContainer.add(item.asWidget());
        });

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

    private final Map<En_ImportanceLevel, SlaRowItem> importanceToItemMap = new HashMap<>();

    interface SlaInputUiBinder extends UiBinder<HTMLPanel, SlaInput> {}
    private static SlaInputUiBinder ourUiBinder = GWT.create(SlaInputUiBinder.class);
}
