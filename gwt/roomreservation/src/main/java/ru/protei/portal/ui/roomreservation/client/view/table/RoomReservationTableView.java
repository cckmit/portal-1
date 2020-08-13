package ru.protei.portal.ui.roomreservation.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_RoomReservationReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.roomreservation.client.activity.table.AbstractRoomReservationTableActivity;
import ru.protei.portal.ui.roomreservation.client.activity.table.AbstractRoomReservationTableView;
import ru.protei.portal.ui.roomreservation.client.widget.filter.RoomReservationParamWidget;

import java.util.ArrayList;
import java.util.List;

public class RoomReservationTableView extends Composite implements AbstractRoomReservationTableView {

    @Inject
    public void onInit(EditClickColumn<RoomReservation> editClickColumn, RemoveClickColumn<RoomReservation> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractRoomReservationTableActivity activity) {
        this.activity = activity;

        filterParam.setOnFilterChangeCallback(activity::onFilterChange);

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        removeClickColumn.setHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);

        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });

        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }

    @Override
    public RoomReservationParamWidget getFilterParam() {
        return filterParam;
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        table.setTotalRecords(totalRecords);
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo( int page ) {
        table.scrollToPage( page );
    }

    @Override
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    private void initTable() {

//        editClickColumn.setDisplayPredicate(value -> AccessUtil.isAllowedEdit(policyService, value));
//        removeClickColumn.setDisplayPredicate(value -> AccessUtil.isAllowedRemove(policyService, value));

        columns.add(reason);
        columns.add(date);
        columns.add(comment);

        table.addColumn(person.header, person.values);
        table.addColumn(date.header, date.values);
        table.addColumn(reason.header, reason.values);
        table.addColumn(comment.header, comment.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);

    }

    ClickColumn<RoomReservation> person = new ClickColumn<RoomReservation>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.accountPerson());
        }

        @Override
        public void fillColumnValue(Element cell, RoomReservation value) {
            cell.setInnerHTML(value.getPersonResponsible().getDisplayName());
        }
    };

    ClickColumn<RoomReservation> date = new ClickColumn<RoomReservation>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.absenceFromTime());
        }

        @Override
        public void fillColumnValue(Element cell, RoomReservation value) {
            cell.setInnerHTML(DateFormatter.formatDateTime(value.getDateFrom()) + " - " + DateFormatter.formatDateTime(value.getDateUntil()));
        }
    };


    ClickColumn<RoomReservation> reason = new ClickColumn<RoomReservation>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.absenceReason());
        }

        @Override
        public void fillColumnValue(Element cell, RoomReservation value) {
            cell.setInnerText(reasonLang.getName(value.getReason()));
        }
    };

    ClickColumn<RoomReservation> comment = new ClickColumn<RoomReservation>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.absenceComment());
        }

        @Override
        public void fillColumnValue(Element cell, RoomReservation value) {
            cell.setInnerHTML(value.getComment());
        }
    };

    @UiField
    InfiniteTableWidget<RoomReservation> table;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    @UiField(provided = true)
    RoomReservationParamWidget filterParam;

    @Inject
    @UiField
    Lang lang;

    @Inject
    En_RoomReservationReasonLang reasonLang;

    @Inject
    PolicyService policyService;

    AbstractRoomReservationTableActivity activity;
    EditClickColumn<RoomReservation> editClickColumn;
    RemoveClickColumn<RoomReservation> removeClickColumn;
    ClickColumnProvider<RoomReservation> columnProvider = new ClickColumnProvider<>();
    List<ClickColumn<RoomReservation>> columns = new ArrayList<>();

    private static RoomReservationTableViewUiBinder ourUiBinder = GWT.create(RoomReservationTableViewUiBinder.class);
    interface RoomReservationTableViewUiBinder extends UiBinder<HTMLPanel, RoomReservationTableView> {}
}