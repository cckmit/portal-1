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
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.filter.AbstractIpReservationFilterView;

/**
 * Представление фильтра подсистемы резервирования IP
 */
public class IpReservationFilterView extends Composite implements AbstractIpReservationFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        sortField.setType(ModuleType.RESERVED_IP);
        //resetFilter();
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
    public HasValue<Subnet> subnet() { return null; }

    @Override
    public HasValue<PersonShortView> owner() { return ipOwner; }

    @Override
    public HasValue<DateInterval> reserveDate() { return reserveDate; }

    @Override
    public HasValue<DateInterval> releaseDate() { return releaseDate; }

    @Override
    public HasValue<DateInterval> lastActiveDate() { return lastActiveDate; }

    @Override
    public HasValue<Boolean> onlyLocal() { return onlyLocal; }

    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.ip_address );
        sortDir.setValue( true );
        search.setValue( "" );
        ipOwner.setValue(null);
/*        subnet.setValue(null);*/
        reserveDate.setValue(null);
        releaseDate.setValue(null);
        lastActiveDate.setValue(null);
        onlyLocal.setValue(false);
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent<En_SortField> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        timer.cancel();
        timer.schedule( 400 );
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    @UiHandler("onlyLocal")
    public void onOnlyLocalClicked(ClickEvent event) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "ipOwner" )
    public void onOwnerSelected( ValueChangeEvent<PersonShortView> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }


    @UiField
    CleanableSearchBox search;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector ipOwner;

/*    @Inject
    @UiField(provided = true)
    SubnetButtonSelector subnet;*/

    @Inject
    @UiField(provided = true)
    RangePicker reserveDate;

    @Inject
    @UiField(provided = true)
    RangePicker releaseDate;

    @Inject
    @UiField(provided = true)
    RangePicker lastActiveDate;

    @UiField
    CheckBox onlyLocal;

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