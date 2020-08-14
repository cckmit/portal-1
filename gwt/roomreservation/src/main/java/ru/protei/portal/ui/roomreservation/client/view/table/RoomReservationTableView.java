package ru.protei.portal.ui.roomreservation.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_RoomReservationReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.roomreservation.client.activity.table.AbstractRoomReservationTableActivity;
import ru.protei.portal.ui.roomreservation.client.activity.table.AbstractRoomReservationTableView;
import ru.protei.portal.ui.roomreservation.client.util.AccessUtil;
import ru.protei.portal.ui.roomreservation.client.widget.filter.RoomReservationFilterWidget;
import ru.protei.portal.ui.roomreservation.client.widget.filter.RoomReservationParamWidget;

import java.util.ArrayList;
import java.util.List;

public class RoomReservationTableView extends Composite implements AbstractRoomReservationTableView {

    @Inject
    public void onInit(EditClickColumn<RoomReservation> editClickColumn,
                       RemoveClickColumn<RoomReservation> removeClickColumn,
                       RoomReservationFilterWidget filterWidget) {
        this.filterWidget = filterWidget;
        this.filterWidget.onInit();
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractRoomReservationTableActivity activity) {
        this.activity = activity;

        filterWidget.setOnFilterChangeCallback(activity::onFilterChange);

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
    public RoomReservationParamWidget getFilterWidget() {
        return filterWidget.getFilterParamView();
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

        editClickColumn.setDisplayPredicate(value -> AccessUtil.canEdit(policyService, value));
        removeClickColumn.setDisplayPredicate(value -> AccessUtil.canRemove(policyService, value));

        DynamicColumn<RoomReservation> person = new DynamicColumn<>(lang.roomReservationPersonResponsible(), "person",
                value -> value.getPersonResponsible().getDisplayName());

        DynamicColumn<RoomReservation> fromTime = new DynamicColumn<>(lang.roomReservationDatesFrom(), "from-time",
                value -> DateFormatter.formatDateTime(value.getDateFrom()));

        DynamicColumn<RoomReservation> tillTime = new DynamicColumn<>(lang.roomReservationDatesTill(), "till-time",
                value -> DateFormatter.formatDateTime(value.getDateUntil()));

        DynamicColumn<RoomReservation> room = new DynamicColumn<>(lang.roomReservationRoom(), "room",
                value -> value.getRoom().getName());

        DynamicColumn<RoomReservation> reason = new DynamicColumn<>(lang.roomReservationReason(), "reason",
                value -> reasonLang.getName(value.getReason()));

        DynamicColumn<RoomReservation> coffee = new DynamicColumn<>(lang.roomReservationCoffeeBreakCount(), "coffee",
                value -> String.valueOf(value.getCoffeeBreakCount()));

        DynamicColumn<RoomReservation> comment = new DynamicColumn<>(lang.roomReservationComment(), "comment",
                value -> value.getComment());

        columns.add(person);
        columns.add(fromTime);
        columns.add(tillTime);
        columns.add(room);
        columns.add(reason);
        columns.add(coffee);
        columns.add(comment);

        table.addColumn(person.header, person.values);
        table.addColumn(fromTime.header, fromTime.values);
        table.addColumn(tillTime.header, tillTime.values);
        table.addColumn(room.header, room.values);
        table.addColumn(reason.header, reason.values);
        table.addColumn(coffee.header, coffee.values);
        table.addColumn(comment.header, comment.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    @UiField
    InfiniteTableWidget<RoomReservation> table;
    @UiField
    HTMLPanel pagerContainer;

    @UiField(provided = true)
    RoomReservationFilterWidget filterWidget;

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