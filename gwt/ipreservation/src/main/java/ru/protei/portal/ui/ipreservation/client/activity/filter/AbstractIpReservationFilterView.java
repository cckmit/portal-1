package ru.protei.portal.ui.ipreservation.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.view.PersonShortView;

/**
 * Представление поиска зарезервированного IP
 */
public interface AbstractIpReservationFilterView extends IsWidget {
    void setActivity( AbstractIpReservationFilterActivity activity);

    HasValue<String> search();
    HasValue<Subnet> subnet();
    HasValue<PersonShortView> owner();
    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();

    void resetFilter();

/*
    HasValue<DateInterval> dateReservedRange();
    HasValue<DateInterval> dateReleasedRange();
    HasValue<DateInterval> dateLastActiveRange();
    */
}
