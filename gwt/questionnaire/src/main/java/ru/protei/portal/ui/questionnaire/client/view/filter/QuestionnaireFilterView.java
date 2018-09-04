package ru.protei.portal.ui.questionnaire.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.issuestate.optionlist.IssueStatesOptionList;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.questionnaire.client.activity.filter.AbstractQuestionnaireFilterActivity;
import ru.protei.portal.ui.questionnaire.client.activity.filter.AbstractQuestionnaireFilterView;

import java.util.HashSet;
import java.util.Set;

public class QuestionnaireFilterView extends Composite implements AbstractQuestionnaireFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        dateRange.setPlaceholder(lang.selectDate());
        sortField.setType(ModuleType.QUESTIONNAIRE);
    }

    @Override
    public void setActivity(AbstractQuestionnaireFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        sortField.setValue(En_SortField.creation_date);
        dateRange.setValue(null);
        sortDir.setValue(false);
        states.setValue(new HashSet<>());
    }

    @Override
    public HasValue<String> searchString() {
        return name;
    }

    @Override
    public HasValue<Set<En_CaseState>> states() {
        return states;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        if (activity != null) {
            resetFilter();
            if (activity != null) {
                activity.onFilterChanged();
            }
        }
    }

    @UiHandler( "name" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        restartChangeTimer();
    }

    @UiHandler("sortDir")
    public void onSortDirChanged(ValueChangeEvent<Boolean> event) {
        restartChangeTimer();
    }

    @UiHandler("sortField")
    public void onSortFieldChanged(ValueChangeEvent<En_SortField> event) {
        restartChangeTimer();
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateInterval> event) {
        restartChangeTimer();
    }

    @UiHandler("states")
    public void onStatesChanged(ValueChangeEvent<Set<En_CaseState>> event) {
        restartChangeTimer();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    private void restartChangeTimer() {
        changeTimer.cancel();
        changeTimer.schedule(300);
    }

    private final Timer changeTimer = new Timer() {
        @Override
        public void run() {
            if (activity != null)
                activity.onFilterChanged();
        }
    };

    @Inject
    @UiField
    Lang lang;

    @UiField
    CleanableSearchBox name;

    @Inject
    @UiField(provided = true)
    IssueStatesOptionList states;

    @Inject
    @UiField(provided = true)
    RangePicker dateRange;

    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @Inject
    FixedPositioner positioner;

    private AbstractQuestionnaireFilterActivity activity;

    private static QuestionnaireFilterViewUiBinder outUiBinder = GWT.create(QuestionnaireFilterViewUiBinder.class);

    interface QuestionnaireFilterViewUiBinder extends UiBinder<HTMLPanel, QuestionnaireFilterView> {
    }
}
