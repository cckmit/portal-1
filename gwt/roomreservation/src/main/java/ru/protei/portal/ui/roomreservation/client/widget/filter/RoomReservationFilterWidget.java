package ru.protei.portal.ui.roomreservation.client.widget.filter;

import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.ui.common.client.widget.filterwidget.simplefilterwidget.SimpleFilterWidget;

import javax.inject.Inject;

public class RoomReservationFilterWidget extends SimpleFilterWidget<RoomReservationQuery> {

    @Inject
    public RoomReservationFilterWidget(RoomReservationParamWidget filterParamView) {
        this.filterParamView = filterParamView;
    }

    @Override
    public RoomReservationParamWidget getFilterParamView() {
        return (RoomReservationParamWidget)this.filterParamView;
    }
}
