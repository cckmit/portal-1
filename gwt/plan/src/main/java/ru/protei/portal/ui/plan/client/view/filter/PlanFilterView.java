package ru.protei.portal.ui.plan.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.plan.client.activity.filter.AbstractPlanFilterActivity;
import ru.protei.portal.ui.plan.client.activity.filter.AbstractPlanFilterView;

public class PlanFilterView extends Composite implements AbstractPlanFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        creationRange.setPlaceholder(lang.selectDate());
        startRange.setPlaceholder(lang.selectDate());
        finishRange.setPlaceholder(lang.selectDate());
        resetFilter();
    }

    @Override
    public void setActivity( AbstractPlanFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<En_SortField> sortField() { return sortField; }

    @Override
    public HasValue<Boolean> sortDir() { return sortDir; }

    @Override
    public HasValue<String> search() { return search; }

    @Override
    public HasValue<PersonShortView> creator() { return planCreator; }

    @Override
    public HasValue<DateInterval> creationRange() { return creationRange; }

    @Override
    public HasValue<DateInterval> startRange() { return startRange; }

    @Override
    public HasValue<DateInterval> finishRange() { return finishRange; }

    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.creation_date );
        sortDir.setValue( true );
        search.setValue( "" );
        planCreator.setValue(null);
        creationRange.setValue(null);
        startRange.setValue(null);
        finishRange.setValue(null);
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        resetFilter();
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent<En_SortField> event )  {
        fireChangeTimer();
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event )  {
        fireChangeTimer();
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event )  {
        fireChangeTimer();
    }

    @UiHandler("planCreator")
    public void onCreatorSelected( ValueChangeEvent<PersonShortView> event )  {
        fireChangeTimer();
    }

    @UiHandler({"creationRange", "startRange", "finishRange"})
    public void onDateChanged(ValueChangeEvent<DateInterval> event) {
        fireChangeTimer();
    }

    private void fireChangeTimer() {
        timer.cancel();
        timer.schedule(300);
    }

    private final Timer timer = new Timer() {
        @Override
        public void run() {
            if (activity != null) {
                activity.onFilterChanged();
            }
        }
    };

    @UiField
    CleanableSearchBox search;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector planCreator;

    @Inject
    @UiField(provided = true)
    RangePicker creationRange;

    @Inject
    @UiField(provided = true)
    RangePicker startRange;

    @Inject
    @UiField(provided = true)
    RangePicker finishRange;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    Button resetBtn;

    @UiField
    Lang lang;

    AbstractPlanFilterActivity activity;

    private static PlanFilterViewUiBinder ourUiBinder = GWT.create(PlanFilterViewUiBinder.class);
    interface PlanFilterViewUiBinder extends UiBinder<HTMLPanel, PlanFilterView> {}
}
