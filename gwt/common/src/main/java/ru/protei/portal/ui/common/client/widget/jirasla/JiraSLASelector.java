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
import ru.protei.portal.core.model.dict.En_JiraSLAIssueTypeEditable;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.JiraMetaData;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SLAControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;
import ru.protei.portal.ui.common.client.widget.selector.text.RawTextButtonSelector;
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
        render();
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<JiraMetaData> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("issueType")
    public void issueTypeChanged(ValueChangeEvent<String> event) {
        changed();
        render();
    }

    @UiHandler("severity")
    public void severityChanged(ValueChangeEvent<String> event) {
        changed();
        render();
    }

    private void render() {

        issueType.fillOptions(Collections.emptyList());
        issueType.setEnabled(false);
        severity.fillOptions(Collections.emptyList());
        severity.setEnabled(false);

        if (value == null) {
            return;
        }

        if (!isCached()) {
            loadCache(value.getSlaMapId(), this::render);
            return;
        }

        String currentIssueType = value.getIssueType();
        String currentSeverity = value.getSeverity();
        Long currentTimeOfReaction = collectTimeOfReaction(cache, currentIssueType, currentSeverity);
        Long currentTimeOfDecision = collectTimeOfDecision(cache, currentIssueType, currentSeverity);
        boolean isSeverityEditable = En_JiraSLAIssueTypeEditable.forIssueType(currentIssueType) != null;

        issueType.fillOptions(collectIssueTypes(cache));
        issueType.setValue(currentIssueType);
        issueType.setEnabled(false);

        if (!isSeverityEditable && StringUtils.isBlank(currentSeverity)) {
            severity.setEnabled(false);
            severityContainer.setVisible(false);
        } else {
            severity.setDisplayOptionCreator(makeSeverityDisplayOptionCreator(currentIssueType, isSeverityEditable));
            severity.fillOptions(collectSeverities(cache, currentIssueType));
            severity.setValue(currentSeverity);
            severity.setEnabled(isSeverityEditable);
            severityContainer.setVisible(true);
        }

        if (currentTimeOfReaction == null) {
            timeOfReactionContainer.setVisible(false);
        } else {
            timeOfReaction.setValue(workTimeFormatter.asString(currentTimeOfReaction));
            timeOfReactionContainer.setVisible(true);
        }

        if (currentTimeOfDecision == null) {
            timeOfDecisionContainer.setVisible(false);
        } else {
            timeOfDecision.setValue(workTimeFormatter.asString(currentTimeOfDecision));
            timeOfDecisionContainer.setVisible(true);
        }
    }

    private void changed() {
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

    private List<String> collectIssueTypes(List<JiraSLAMapEntry> data) {
        return data.stream()
            .map(JiraSLAMapEntry::getIssueType)
            .distinct()
            .collect(Collectors.toList());
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

    private DisplayOptionCreator<String> makeSeverityDisplayOptionCreator(String issueType, boolean isSeverityEditable) {
        return (value) -> {
            String description = cache.stream()
                .filter(entry -> Objects.equals(entry.getIssueType(), issueType))
                .filter(entry -> Objects.equals(entry.getSeverity(), value))
                .map(JiraSLAMapEntry::getDescription)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("");
            boolean isDescriptionAvailable = StringUtils.isNotBlank(description);
            if (isSeverityEditable) {
                return new DisplayOption(isDescriptionAvailable ? description : value);
            } else {
                return new DisplayOption(value + (isDescriptionAvailable ? " (" + description + ")" : ""));
            }
        };
    }

    @Inject
    SLAControllerAsync slaController;

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    RawTextButtonSelector issueType;
    @Inject
    @UiField(provided = true)
    RawTextButtonSelector severity;
    @UiField
    TextBox timeOfReaction;
    @UiField
    TextBox timeOfDecision;
    @UiField
    HTMLPanel severityContainer;
    @UiField
    HTMLPanel timeOfReactionContainer;
    @UiField
    HTMLPanel timeOfDecisionContainer;

    private JiraMetaData value;
    private List<JiraSLAMapEntry> cache;

    private WorkTimeFormatter workTimeFormatter;
    interface JiraSLASelectorUiBinder extends UiBinder<HTMLPanel, JiraSLASelector> {}
    private static JiraSLASelectorUiBinder ourUiBinder = GWT.create(JiraSLASelectorUiBinder.class);
}
