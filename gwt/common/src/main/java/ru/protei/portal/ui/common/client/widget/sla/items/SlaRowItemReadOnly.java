package ru.protei.portal.ui.common.client.widget.sla.items;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.timefield.WorkTimeFormatter;

public class SlaRowItemReadOnly extends Composite implements HasValue<ProjectSla> {
    @Inject
    public SlaRowItemReadOnly(Lang lang) {
        workTimeFormatter = new WorkTimeFormatter(lang);
        workTimeFormatter.setFullDayTime(true);
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public ProjectSla getValue() {
        return projectSla;
    }

    @Override
    public void setValue(ProjectSla value) {
        setValue(value, false);
    }

    @Override
    public void setValue(ProjectSla value, boolean fireEvents) {
        this.projectSla = value;

        reactionTime.setInnerText(format(value.getReactionTime()));
        temporarySolutionTime.setInnerText(format(value.getTemporarySolutionTime()));
        fullSolutionTime.setInnerText(format(value.getFullSolutionTime()));

        this.importance.setInnerText(projectSla.getImportanceCode());

        if (fireEvents) {
            ValueChangeEvent.fire(this, projectSla);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ProjectSla> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void clear() {
        reactionTime.setInnerText("");
        temporarySolutionTime.setInnerText("");
        fullSolutionTime.setInnerText("");
    }

    private String format(Long value) {
        return value == null ? "" : workTimeFormatter.asString(value);
    }

    @UiField
    LabelElement importance;

    @UiField
    SpanElement reactionTime;

    @UiField
    SpanElement temporarySolutionTime;

    @UiField
    SpanElement fullSolutionTime;

    private ProjectSla projectSla;
    private final WorkTimeFormatter workTimeFormatter;

    interface SlaRowItemReadOnlyUiBinder extends UiBinder<HTMLPanel, SlaRowItemReadOnly> {}
    private static SlaRowItemReadOnlyUiBinder ourUiBinder = GWT.create(SlaRowItemReadOnlyUiBinder.class);
}
