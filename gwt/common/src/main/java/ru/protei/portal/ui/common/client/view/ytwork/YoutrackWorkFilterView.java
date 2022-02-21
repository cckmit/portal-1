package ru.protei.portal.ui.common.client.view.ytwork;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.ui.common.client.activity.ytwork.AbstractYoutrackWorkFilterActivity;
import ru.protei.portal.ui.common.client.activity.ytwork.AbstractYoutrackWorkFilterView;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackWorkDictionaryTableView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

import java.util.*;

public class YoutrackWorkFilterView extends Composite implements AbstractYoutrackWorkFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        date.fillSelector(En_DateIntervalType.defaultTypes());
    }

    @Override
    public void setActivity(AbstractYoutrackWorkFilterActivity activity) {
        this.activity = activity;

        AbstractYoutrackWorkDictionaryTableView dictionaryTable = activity.getDictionaryTable(En_YoutrackWorkType.NIOKR);
        tablesViews.add(dictionaryTable);
        tables.add(addCol(dictionaryTable.asWidget()));

        dictionaryTable = activity.getDictionaryTable(En_YoutrackWorkType.NMA);
        tablesViews.add(dictionaryTable);
        tables.add(addCol(dictionaryTable.asWidget()));
    }

    private Widget addCol(Widget widget) {
        SimplePanel widgets = new SimplePanel(widget);
        widgets.setStyleName("col-lg-12 col-xl-6 col-xlg-6");
        return widgets;
    }

    @Override
    public void resetFilter(boolean withRefreshTables) {
        date.setValue(null);
        if (withRefreshTables) {
            tablesViews.forEach(tableView -> {
                tableView.setCollapsed(true);
                tableView.refreshTable();
            });
        }
    }

    @Override
    public void clearFooterStyle() {
        footer.removeClassName("card-footer");
    }

    @Override
    public HasValue<DateIntervalWithType> date() {
        return date;
    }

    @Override
    public void setDateValid(boolean isTypeValid, boolean isRangeValid) {
        date.setValid(isTypeValid, isRangeValid);
    }

    @UiHandler("date")
    public void onDateRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        validate();
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        resetFilter(true);
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    private boolean validate() {
        return isDateRangeValid(date.getValue());
    }

    public boolean isDateRangeValid(DateIntervalWithType dateRange) {
        if (dateRange == null || dateRange.getIntervalType() == null) {
            return true;
        }

        return !Objects.equals(dateRange.getIntervalType(), En_DateIntervalType.FIXED) || dateRange.getInterval().isValid();
    }
    

    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker date;

    @UiField
    HTMLPanel tables;

    @UiField
    DivElement footer;

    private AbstractYoutrackWorkFilterActivity activity;
    private List<AbstractYoutrackWorkDictionaryTableView> tablesViews = new ArrayList<>();

    private static FilterViewUiBinder outUiBinder = GWT.create(FilterViewUiBinder.class);
    interface FilterViewUiBinder extends UiBinder<HTMLPanel, YoutrackWorkFilterView> {}
}
