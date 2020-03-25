package ru.protei.portal.ui.ipreservation.client.view.filter;

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
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterView;
import ru.protei.portal.ui.ipreservation.client.view.widget.selector.SubnetMultiSelector;

import java.util.Set;

/**
 * Представление фильтра подсистемы резервирования IP
 */
public class IpReservationFilterView extends Composite implements AbstractIpReservationFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        resetFilter();
    }

    @Override
    public void setActivity( AbstractIpReservationFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<En_SortField> sortField() { return sortField; }

    @Override
    public HasValue<Boolean> sortDir() { return sortDir; }

    @Override
    public HasValue<String> search() { return search; }

    @Override
    public HasValue<Set<SubnetOption>> subnets() {
        return subnets;
    }

    @Override
    public HasValue<PersonShortView> owner() { return ipOwner; }

    @Override
    public HasValue<DateInterval> reserveDate() { return reserveDate; }

    @Override
    public HasValue<DateInterval> releaseDate() { return releaseDate; }

    @Override
    public HasValue<DateInterval> lastActiveDate() { return lastActiveDate; }

    @Override
    public HasVisibility ownerVisibility() {
        return ipOwner;
    }

    @Override
    public HasVisibility subnetsVisibility() {
        return subnets;
    }

    @Override
    public HasVisibility datesVisibility() {
        return reserveDate || releaseDate || ;
    }


    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.ip_address );
        sortDir.setValue( true );
        search.setValue( "" );
        ipOwner.setValue(null);
        subnets.setValue(null);
        reserveDate.setValue(null);
        releaseDate.setValue(null);
        lastActiveDate.setValue(null);
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

    @UiHandler( "ipOwner" )
    public void onOwnerSelected( ValueChangeEvent<PersonShortView> event )  {
        fireChangeTimer();
    }

    @UiHandler("subnets")
    public void onSubnetSelected(ValueChangeEvent<Set<SubnetOption>> event)  {
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
    EmployeeButtonSelector ipOwner;

    @Inject
    @UiField(provided = true)
    SubnetMultiSelector subnets;

    @Inject
    @UiField(provided = true)
    RangePicker reserveDate;

    @Inject
    @UiField(provided = true)
    RangePicker releaseDate;

    @Inject
    @UiField(provided = true)
    RangePicker lastActiveDate;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;

    AbstractIpReservationFilterActivity activity;

    private static IpReservationFilterViewUiBinder ourUiBinder = GWT.create( IpReservationFilterViewUiBinder.class );
    interface IpReservationFilterViewUiBinder extends UiBinder<HTMLPanel, IpReservationFilterView> {}
}