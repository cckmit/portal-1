package ru.protei.portal.ui.common.client.widget.sla;

import com.google.gwt.core.client.GWT;
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
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.timefield.WorkTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SlaInputReadOnly extends Composite implements HasValue<List<ProjectSla>> {
    @Inject
    public SlaInputReadOnly(Lang lang) {
        workTimeFormatter = new WorkTimeFormatter(lang);
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public List<ProjectSla> getValue() {
        return projectSlas;
    }

    @Override
    public void setValue(List<ProjectSla> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(List<ProjectSla> value, boolean fireEvents) {
        if (CollectionUtils.isEmpty(value)) {
            projectSlas = Collections.emptyList();

            criticalReactionTime.setInnerText("");
            criticalTemporarySolution.setInnerText("");
            criticalFullSolution.setInnerText("");

            importantReactionTime.setInnerText("");
            importantTemporarySolution.setInnerText("");
            importantFullSolution.setInnerText("");

            basicReactionTime.setInnerText("");
            basicTemporarySolution.setInnerText("");
            basicFullSolution.setInnerText("");

            cosmeticReactionTime.setInnerText("");
            cosmeticTemporarySolution.setInnerText("");
            cosmeticFullSolution.setInnerText("");

        } else {
            projectSlas = new ArrayList<>(value);
            projectSlas.forEach(sla -> {
                if (sla.getImportanceLevelId() == En_ImportanceLevel.CRITICAL.getId()) {
                    criticalReactionTime.setInnerText(format(sla.getReactionTime()));
                    criticalTemporarySolution.setInnerText(format(sla.getTemporarySolutionTime()));
                    criticalFullSolution.setInnerText(format(sla.getFullSolutionTime()));
                }

                if (sla.getImportanceLevelId() == En_ImportanceLevel.IMPORTANT.getId()) {
                    importantReactionTime.setInnerText(format(sla.getReactionTime()));
                    importantTemporarySolution.setInnerText(format(sla.getTemporarySolutionTime()));
                    importantFullSolution.setInnerText(format(sla.getFullSolutionTime()));
                }

                if (sla.getImportanceLevelId() == En_ImportanceLevel.BASIC.getId()) {
                    basicReactionTime.setInnerText(format(sla.getReactionTime()));
                    basicTemporarySolution.setInnerText(format(sla.getTemporarySolutionTime()));
                    basicFullSolution.setInnerText(format(sla.getFullSolutionTime()));
                }

                if (sla.getImportanceLevelId() == En_ImportanceLevel.COSMETIC.getId()) {
                    cosmeticReactionTime.setInnerText(format(sla.getReactionTime()));
                    cosmeticTemporarySolution.setInnerText(format(sla.getTemporarySolutionTime()));
                    cosmeticFullSolution.setInnerText(format(sla.getFullSolutionTime()));
                }
            });
        }

        if (fireEvents) {
            ValueChangeEvent.fire(this, projectSlas);
        }
    }

    private String format(Long value) {
        return value == null ? "" : workTimeFormatter.asString(value);
    }

    @UiField
    SpanElement criticalReactionTime;

    @UiField
    SpanElement criticalTemporarySolution;

    @UiField
    SpanElement criticalFullSolution;

    @UiField
    SpanElement importantReactionTime;

    @UiField
    SpanElement importantTemporarySolution;

    @UiField
    SpanElement importantFullSolution;

    @UiField
    SpanElement basicReactionTime;

    @UiField
    SpanElement basicTemporarySolution;

    @UiField
    SpanElement basicFullSolution;

    @UiField
    SpanElement cosmeticReactionTime;

    @UiField
    SpanElement cosmeticTemporarySolution;

    @UiField
    SpanElement cosmeticFullSolution;

    private WorkTimeFormatter workTimeFormatter;

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ProjectSla>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private List<ProjectSla> projectSlas;

    interface SlaInputReadOnlyUiBinder extends UiBinder<HTMLPanel, SlaInputReadOnly> {
    }
    private static SlaInputReadOnlyUiBinder ourUiBinder = GWT.create(SlaInputReadOnlyUiBinder.class);
}