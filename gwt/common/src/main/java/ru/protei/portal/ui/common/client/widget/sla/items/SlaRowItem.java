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
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.timefield.TimeTextBox;

public class SlaRowItem extends Composite implements HasValue<ProjectSla> {
    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public ProjectSla getValue() {
        ProjectSla projectSla = new ProjectSla();
        projectSla.setReactionTime(reactionTime.getTime());
        projectSla.setTemporarySolutionTime(temporarySolutionTime.getTime());
        projectSla.setFullSolutionTime(fullSolutionTime.getTime());
        projectSla.setImportanceLevelId(importanceLevel.getId());

        return projectSla;
    }

    @Override
    public void setValue(ProjectSla value) {
        setValue(value, false);
    }

    @Override
    public void setValue(ProjectSla value, boolean fireEvents) {
        if (value != null) {
            reactionTime.setTime(value.getReactionTime());
            temporarySolutionTime.setTime(value.getTemporarySolutionTime());
            fullSolutionTime.setTime(value.getFullSolutionTime());
        }

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ProjectSla> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setImportance(En_ImportanceLevel importance) {
        this.importanceLevel = importance;
        this.importance.setInnerText(importanceLang.getImportanceName(importanceLevel));
    }

    public void clear() {
        reactionTime.setTime(null);
        temporarySolutionTime.setTime(null);
        fullSolutionTime.setTime(null);
    }

    public void setEnsureDebugId(String debugId) {
        String importanceName = importanceLang.getImportanceName(importanceLevel);

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

    private En_ImportanceLevel importanceLevel;

    @Inject
    private En_CaseImportanceLang importanceLang;

    interface SlaRowItemUiBinder extends UiBinder<HTMLPanel, SlaRowItem> {
    }

    private static SlaRowItemUiBinder ourUiBinder = GWT.create(SlaRowItemUiBinder.class);
}