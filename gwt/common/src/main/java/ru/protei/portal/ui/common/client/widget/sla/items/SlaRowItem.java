package ru.protei.portal.ui.common.client.widget.sla.items;

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
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public class SlaRowItem extends Composite implements HasValue<ProjectSla>, HasValidable {
    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public ProjectSla getValue() {
        projectSla.setReactionTime(reactionTime.getTime());
        projectSla.setTemporarySolutionTime(temporarySolutionTime.getTime());
        projectSla.setFullSolutionTime(fullSolutionTime.getTime());

        return projectSla;
    }

    @Override
    public void setValue(ProjectSla value) {
        setValue(value, false);
    }

    @Override
    public void setValue(ProjectSla value, boolean fireEvents) {
        projectSla = value;

        reactionTime.setTime(value.getReactionTime());
        temporarySolutionTime.setTime(value.getTemporarySolutionTime());
        fullSolutionTime.setTime(value.getFullSolutionTime());
        importance.setInnerText(importanceLang.getImportanceName(projectSla.getImportanceLevel()));

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ProjectSla> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void setValid(boolean isValid) {
        reactionTime.setValid(isValid);
        temporarySolutionTime.setValid(isValid);
        fullSolutionTime.setValid(isValid);
    }

    @Override
    public boolean isValid() {
        if (!reactionTime.isValid()) {
            return false;
        }

        if (!temporarySolutionTime.isValid()) {
            return false;
        }

        if (!fullSolutionTime.isValid()) {
            return false;
        }

        return true;
    }

    public void setEnsureDebugId(String debugId) {
        String importanceName = importanceLang.getImportanceName(projectSla.getImportanceLevel());

        reactionTime.ensureDebugId(debugId + "-" + importanceName + "-reaction-time");
        temporarySolutionTime.ensureDebugId(debugId + "-" + importanceName + "-temporary-solution-time");
        fullSolutionTime.ensureDebugId(debugId + "-" + importanceName + "-complete-solution-time");
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

    @Inject
    private En_CaseImportanceLang importanceLang;

    private ProjectSla projectSla;

    interface SlaRowItemUiBinder extends UiBinder<HTMLPanel, SlaRowItem> {}
    private static SlaRowItemUiBinder ourUiBinder = GWT.create(SlaRowItemUiBinder.class);
}
