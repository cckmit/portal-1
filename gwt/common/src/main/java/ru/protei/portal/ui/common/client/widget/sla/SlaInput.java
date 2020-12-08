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
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.ui.common.client.widget.sla.items.SlaRowItem;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

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
        slaRowItems.clear();
        itemsContainer.clear();

        emptyIfNull(value).forEach(projectSla -> {
            SlaRowItem item = slaRowItemProvider.get();
            item.setValue(projectSla);
            slaRowItems.add(item);
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
        for (SlaRowItem item : slaRowItems) {
            item.setValid(isValid);
        }
    }

    @Override
    public boolean isValid() {
        return slaRowItems.stream().allMatch(SlaRowItem::isValid);
    }

    public void setEnsureDebugId(String debugId) {
        for (SlaRowItem item : slaRowItems) {
            item.setEnsureDebugId(debugId);
        }
    }

    private List<ProjectSla> collectSla() {
        return slaRowItems
                .stream()
                .map(SlaRowItem::getValue)
                .collect(Collectors.toList());
    }

    @UiField
    HTMLPanel itemsContainer;

    @Inject
    private Provider<SlaRowItem> slaRowItemProvider;

    private final List<SlaRowItem> slaRowItems = new ArrayList<>();

    interface SlaInputUiBinder extends UiBinder<HTMLPanel, SlaInput> {}
    private static SlaInputUiBinder ourUiBinder = GWT.create(SlaInputUiBinder.class);
}
