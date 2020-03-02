package ru.protei.portal.ui.ipreservation.client.activity.filter;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Активность поиска проекта
 */
public abstract class IpReservationFilterActivity implements Activity, AbstractIpReservationFilterActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ProjectEvents.Search event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
    }

    @Override
    public void onSearchClicked() {
        requestReservedIp();
    }

    @Override
    public void onResetClicked() {
        view.resetFilter();
    }

    private void requestReservedIp() {
        ReservedIpQuery query = makeQuery();
        ipReservationService.getReservedIpList(query, new FluentCallback<List<ReservedIp>>()
                .withErrorMessage(lang.errGetList())
                .withSuccess(result -> {
                    view.fillReservedIpList(result);
                }));
    }

    private ReservedIpQuery makeQuery() {
        ReservedIpQuery query = new ReservedIpQuery(view.search().getValue(), En_SortField.ip_address, En_SortDir.DESC);
        DateInterval reservedInterval = view.dateReservedRange().getValue();
        if (reservedInterval != null) {
            query.setReservedFrom(reservedInterval.from);
            query.setReservedTo(reservedInterval.to);
        }
        DateInterval releasedInterval = view.dateReleasedRange().getValue();
        if (releasedInterval != null) {
            query.setReleasedFrom(releasedInterval.from);
            query.setReleasedTo(releasedInterval.to);
        }
        query.setOwnerIds(view.employee().getValue().stream().map(emp -> emp.getId()).collect(Collectors.toSet()));
        query.setLimit(100);
        return query;
    }

    @Inject
    IpReservationControllerAsync ipReservationService;

    @Inject
    Lang lang;

    @Inject
    AbstractIpReservationFilterView view;
}
