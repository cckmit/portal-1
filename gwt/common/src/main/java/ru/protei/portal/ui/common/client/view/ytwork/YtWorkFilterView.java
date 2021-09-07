package ru.protei.portal.ui.common.client.view.ytwork;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.ui.common.client.activity.ytwork.AbstractYtWorkFilterActivity;
import ru.protei.portal.ui.common.client.activity.ytwork.AbstractYtWorkFilterView;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackReportDictionaryTableView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

public class YtWorkFilterView extends Composite implements AbstractYtWorkFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        date.fillSelector(En_DateIntervalType.defaultTypes());
    }

    @Override
    public void setActivity(AbstractYtWorkFilterActivity activity) {
        this.activity = activity;
        tables.add(addCol(activity.getDictionaryTable(En_ReportYoutrackWorkType.NIOKR).asWidget()));
        tables.add(addCol(activity.getDictionaryTable(En_ReportYoutrackWorkType.NMA).asWidget()));
    }

    private Widget addCol(Widget widget) {
        SimplePanel widgets = new SimplePanel(widget);
        widgets.setStyleName("col-lg-12 col-xl-6 col-xlg-6");
        return widgets;
    }

    @Override
    public void resetFilter() {
        date.setValue(null);
        tables.forEach(widget -> {
            AbstractYoutrackReportDictionaryTableView tableView = (AbstractYoutrackReportDictionaryTableView)widget;
            tableView.setCollapsed(true);
            tableView.onShow();
        });
    }

    @Override
    public void clearFooterStyle() {
        footer.removeClassName("card-footer");
    }

    @Override
    public HasValue<DateIntervalWithType> date() {
        return date;
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        resetFilter();
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("date")
    public void onDateChanged(ValueChangeEvent<DateIntervalWithType> event) {
        restartChangeTimer();
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

    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker date;

    @UiField
    HTMLPanel tables;

    @UiField
    DivElement footer;

    private AbstractYtWorkFilterActivity activity;

    private static FilterViewUiBinder outUiBinder = GWT.create(FilterViewUiBinder.class);
    interface FilterViewUiBinder extends UiBinder<HTMLPanel, YtWorkFilterView> {}
}
