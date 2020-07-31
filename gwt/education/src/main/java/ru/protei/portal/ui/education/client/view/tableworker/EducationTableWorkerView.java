package ru.protei.portal.ui.education.client.view.tableworker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.ui.common.client.columns.ActionIconClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.EducationEntryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.education.client.activity.tableworker.AbstractEducationTableWorkerActivity;
import ru.protei.portal.ui.education.client.activity.tableworker.AbstractEducationTableWorkerView;
import ru.protei.portal.ui.education.client.view.tableworker.column.EducationEntryColumn;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class EducationTableWorkerView extends Composite implements AbstractEducationTableWorkerView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEducationTableWorkerActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void putRecords(List<EducationEntry> list) {
        list.forEach(table::addRow);
    }

    @Override
    public void setTotalRecords(int totalRecords) {
    }

    @Override
    public void showLoader(boolean isShow) {
        loading.removeStyleName("d-block");
        if (isShow) {
            loading.addStyleName("d-block");
        }
    }

    @Override
    public void showRequestEntryAction(boolean isShow) {
        requestEntry.setVisible(isShow);
    }

    @Override
    public void showRequestAttendanceAction(boolean isShow) {
        requestAttendanceAction = isShow;
        initTable();
    }

    private void initTable() {

        table.clearColumns();
        columnProvider = new ClickColumnProvider<>();

        ClickColumn<EducationEntry> entryColumn = new ClickColumn<EducationEntry>() {
            protected void fillColumnHeader(Element columnHeader) {}
            protected void fillColumnValue(Element cell, EducationEntry value) {
                EducationEntryColumn column = educationEntryColumnProvider.get();
                column.setImage(value.getImage());
                column.setLink(value.getLink());
                column.setTitle(value.getTitle());
                column.setType(educationEntryTypeLang.getName(value.getType()));
                column.setCoins(value.getCoins());
                column.setAttendance(getAttendance(value));
                column.setDateAndLocation(getDateAndLocation(value));
                cell.appendChild(column.getElement());
            }
        };
        table.addColumn(entryColumn.header, entryColumn.values);
        entryColumn.setColumnProvider(columnProvider);

        if (requestAttendanceAction) {
            ActionIconClickColumn<EducationEntry> requestColumn = new ActionIconClickColumn<>("fas fa-lg fa-user-plus", null, null);
            table.addColumn(requestColumn.header, requestColumn.values);
            requestColumn.setActionHandler(value -> activity.requestAttendance(value));
            requestColumn.setColumnProvider(columnProvider);
        }
    }

    private String getAttendance(EducationEntry value) {
        int limit = 5;
        int size = emptyIfNull(value.getAttendanceList()).size();
        return stream(value.getAttendanceList())
                .filter(EducationEntryAttendance::isApproved)
                .map(EducationEntryAttendance::getWorkerName)
                .limit(limit)
                .collect(Collectors.joining(", "))
                + (size > limit ? " +" + (size - limit) : "");
    }

    private String getDateAndLocation(EducationEntry value) {
        String location = value.getLocation();
        String start = value.getDateStart() != null
                ? dateFormat.format(value.getDateStart())
                : "";
        String end = value.getDateEnd() != null
                ? dateFormat.format(value.getDateEnd())
                : "";
        String dates = prettyDateRange(start, end);
        if (isEmpty(location) && isEmpty(dates)) {
            return "";
        }
        if (isEmpty(dates)) {
            return location;
        }
        if (isEmpty(location)) {
            return dates;
        }
        return dates + " | " + location;
    }

    private String prettyDateRange(String start, String end) {
        if (Objects.equals(start, end)) {
            return start;
        }
        if (isEmpty(start)) {
            return end;
        }
        return start + " - " + end;
    }

    @UiHandler("requestEntry")
    public void requestEntryClick(ClickEvent event) {
        if (activity != null) {
            activity.requestEntry();
        }
    }

    @Inject
    @UiField
    Lang lang;
    @Inject
    EducationEntryTypeLang educationEntryTypeLang;
    @Inject
    Provider<EducationEntryColumn> educationEntryColumnProvider;

    @UiField
    TableWidget<EducationEntry> table;
    @UiField
    IndeterminateCircleLoading loading;
    @UiField
    Button requestEntry;

    private boolean requestAttendanceAction = false;
    private ClickColumnProvider<EducationEntry> columnProvider;
    private AbstractEducationTableWorkerActivity activity;
    private static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.MM.yyyy");

    interface EducationTableWorkerViewBinder extends UiBinder<HTMLPanel, EducationTableWorkerView> {}
    private static EducationTableWorkerViewBinder ourUiBinder = GWT.create(EducationTableWorkerViewBinder.class);
}
