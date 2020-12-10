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
import ru.protei.portal.ui.common.client.widget.sla.items.SlaRowItemReadOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public class SlaInputReadOnly extends Composite implements HasValue<List<ProjectSla>> {
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
            SlaRowItemReadOnly item = slaRowItemReadOnlyProvider.get();
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

    private List<ProjectSla> collectSla() {
        return slaRowItems
                .stream()
                .map(SlaRowItemReadOnly::getValue)
                .collect(Collectors.toList());
    }

    @UiField
    HTMLPanel itemsContainer;

    @Inject
    private Provider<SlaRowItemReadOnly> slaRowItemReadOnlyProvider;

    private final List<SlaRowItemReadOnly> slaRowItems = new ArrayList<>();

    interface SlaInputReadOnlyUiBinder extends UiBinder<HTMLPanel, SlaInputReadOnly> {}
    private static SlaInputReadOnlyUiBinder ourUiBinder = GWT.create(SlaInputReadOnlyUiBinder.class);
}
