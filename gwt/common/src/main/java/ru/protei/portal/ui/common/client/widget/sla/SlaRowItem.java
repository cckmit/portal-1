package ru.protei.portal.ui.common.client.widget.sla;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ContractSla;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;

public class SlaRowItem extends Composite implements HasValue<ContractSla> {
    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public ContractSla getValue() {
        ContractSla contractSla = new ContractSla();
        contractSla.setReactionTime(reactionTime.getTime());
        contractSla.setTemporarySolutionTime(temporarySolutionTime.getTime());
        contractSla.setFullSolutionTime(fullSolutionTime.getTime());

        return contractSla;
    }

    @Override
    public void setValue(ContractSla value) {
        setValue(value, false);
    }

    @Override
    public void setValue(ContractSla value, boolean fireEvents) {
        if (value == null) {
            reactionTime.setTime(null);
            temporarySolutionTime.setTime(null);
            fullSolutionTime.setTime(null);

            return;
        }

        reactionTime.setTime(value.getReactionTime());
        temporarySolutionTime.setTime(value.getTemporarySolutionTime());
        fullSolutionTime.setTime(value.getFullSolutionTime());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ContractSla> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setImportance(String importance) {
        this.importance.setInnerText(importance);
    }

    public void setEnsureDebugId(String debugId) {
        reactionTime.ensureDebugId(debugId + "-reaction-time");
        temporarySolutionTime.ensureDebugId(debugId + "-temporary-solution-time");
        fullSolutionTime.ensureDebugId(debugId + "-complete-solution-time");
    }

    @UiField
    LabelElement importance;

    @Inject
    @UiField(provided = true)
    TimeTextBox reactionTime;

    @Inject
    @UiField(provided = true)
    TimeTextBox temporarySolutionTime;

    @Inject
    @UiField(provided = true)
    TimeTextBox fullSolutionTime;

    interface SlaRowItemUiBinder extends UiBinder<HTMLPanel, SlaRowItem> {
    }

    private static SlaRowItemUiBinder ourUiBinder = GWT.create(SlaRowItemUiBinder.class);
}