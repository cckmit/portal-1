package ru.protei.portal.ui.common.client.widget.jirasla;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_JiraSLAIssueType;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.JiraMetaData;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SLAControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;
import ru.protei.portal.ui.common.client.widget.selector.text.RawTextButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.text.RawTextFormSelector;
import ru.protei.portal.ui.common.client.widget.timefield.WorkTimeFormatter;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JiraSLASelector extends Composite implements HasValue<JiraMetaData>, HasVisibility {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        workTimeFormatter = new WorkTimeFormatter(lang);
    }

    @Override
    public JiraMetaData getValue() {
        return value;
    }

    @Override
    public void setValue(JiraMetaData value) {
        setValue(value, false);
    }

    @Override
    public void setValue(JiraMetaData value, boolean fireEvents) {
        clearCache();
        this.value = value;
        renderView();
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<JiraMetaData> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("severity")
    public void severityChanged(ValueChangeEvent<String> event) {
        fillValueFromView();
        renderView();
    }

    private void renderView() {
        renderViewDisableAll();

        if (value == null) {
            return;
        }

        if (!isCached()) {
            loadCache(value.getSlaMapId(), this::renderView);
            return;
        }

        String currentIssueType = value.getIssueType();
        String currentSeverity = value.getSeverity();
        Long currentTimeOfReaction = collectTimeOfReaction(cache, currentIssueType, currentSeverity);
        Long currentTimeOfDecision = collectTimeOfDecision(cache, currentIssueType, currentSeverity);

        renderViewIssueType(currentIssueType);
        renderViewSeverity(currentIssueType, currentSeverity);
        renderViewTimeOfReaction(currentTimeOfReaction);
        renderViewTimeOfDecision(currentTimeOfDecision);
    }

    private void renderViewDisableAll() {
        issueType.setValue("");
        issueType.setEnabled(false);
        severity.fillOptions(Collections.emptyList());
        severity.setEnabled(false);
    }

    private void renderViewIssueType(String currentIssueType) {
        issueType.setValue(currentIssueType);
        issueType.setEnabled(false);
    }

    private void renderViewSeverity(String currentIssueType, String currentSeverity) {
        boolean isSeverityEditable = En_JiraSLAIssueType.byPortal().contains(En_JiraSLAIssueType.forIssueType(currentIssueType));
        if (!isSeverityEditable) {
            severity.setEnabled(false);
            severity.setValue("");
        } else {
            severity.setDisplayOptionCreator(makeSeverityDisplayOptionCreator(cache, currentIssueType));
            severity.fillOptions(collectSeverities(cache, currentIssueType));
            severity.setValue(currentSeverity);
            severity.setEnabled(true);
        }
    }

    private void renderViewTimeOfReaction(Long currentTimeOfReaction) {
        timeOfReaction.setValue(currentTimeOfReaction == null ? "" : workTimeFormatter.asString(currentTimeOfReaction));
    }

    private void renderViewTimeOfDecision(Long currentTimeOfDecision) {
        timeOfDecision.setValue(currentTimeOfDecision == null ? "" : workTimeFormatter.asString(currentTimeOfDecision));
    }

    private void fillValueFromView() {
        value.setIssueType(issueType.getValue());
        value.setSeverity(severity.getValue());
    }

    private void loadCache(long mapId, Runnable onLoaded) {
        slaController.getJiraSLAEntries(mapId, new FluentCallback<List<JiraSLAMapEntry>>()
            .withSuccess(list -> {
                cache = list == null ? Collections.emptyList() : list;
                onLoaded.run();
            })
        );
    }

    private boolean isCached() {
        return cache != null;
    }

    private void clearCache() {
        cache = null;
    }

    private List<String> collectSeverities(List<JiraSLAMapEntry> data, String issueType) {
        return data.stream()
            .filter(entry -> Objects.equals(entry.getIssueType(), issueType))
            .map(JiraSLAMapEntry::getSeverity)
            .distinct()
            .collect(Collectors.toList());
    }

    private Long collectTimeOfReaction(List<JiraSLAMapEntry> data, String issueType, String severity) {
        return data.stream()
            .filter(entry -> Objects.equals(entry.getIssueType(), issueType))
            .filter(entry -> Objects.equals(entry.getSeverity(), severity))
            .map(JiraSLAMapEntry::getTimeOfReactionMinutes)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    private Long collectTimeOfDecision(List<JiraSLAMapEntry> data, String issueType, String severity) {
        return data.stream()
            .filter(entry -> Objects.equals(entry.getIssueType(), issueType))
            .filter(entry -> Objects.equals(entry.getSeverity(), severity))
            .map(JiraSLAMapEntry::getTimeOfDecisionMinutes)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    private String collectDescriptionForSeverity(List<JiraSLAMapEntry> data, String issueType, String severity) {
        return data.stream()
            .filter(entry -> Objects.equals(entry.getIssueType(), issueType))
            .filter(entry -> Objects.equals(entry.getSeverity(), severity))
            .map(JiraSLAMapEntry::getDescription)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");
    }

    private DisplayOptionCreator<String> makeSeverityDisplayOptionCreator(List<JiraSLAMapEntry> data, String issueType) {
        return (severity) -> {
            String description = collectDescriptionForSeverity(data, issueType, severity);
            String value = StringUtils.isNotBlank(description) ? description : severity;
            return new DisplayOption(value);
        };
    }

    @Inject
    SLAControllerAsync slaController;

    @Inject
    @UiField
    Lang lang;

    @UiField
    TextBox issueType;
    @Inject
    @UiField(provided = true)
    RawTextFormSelector severity;
    @UiField
    TextBox timeOfReaction;
    @UiField
    TextBox timeOfDecision;

    private JiraMetaData value;
    private List<JiraSLAMapEntry> cache;

    private WorkTimeFormatter workTimeFormatter;
    interface JiraSLASelectorUiBinder extends UiBinder<HTMLPanel, JiraSLASelector> {}
    private static JiraSLASelectorUiBinder ourUiBinder = GWT.create(JiraSLASelectorUiBinder.class);
}
