package ru.protei.portal.ui.ipreservation.client.view.subnet.filter;

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
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.filter.AbstractReservedIpFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.filter.AbstractReservedIpFilterView;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.filter.AbstractSubnetFilterActivity;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.filter.AbstractSubnetFilterView;
import ru.protei.portal.ui.ipreservation.client.view.widget.selector.SubnetMultiSelector;

import java.util.Set;

/**
 * Представление фильтра подсетей
 */
public class SubnetFilterView extends Composite implements AbstractSubnetFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        resetFilter();
    }

    @Override
    public void setActivity( AbstractSubnetFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<En_SortField> sortField() { return sortField; }

    @Override
    public HasValue<Boolean> sortDir() { return sortDir; }

    @Override
    public HasValue<String> search() { return search; }

    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.address );
        sortDir.setValue( true );
        search.setValue( "" );
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
    @UiField( provided = true )
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;

    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;

    AbstractSubnetFilterActivity activity;

    private static SubnetFilterViewUiBinder ourUiBinder = GWT.create( SubnetFilterViewUiBinder.class );
    interface SubnetFilterViewUiBinder extends UiBinder<HTMLPanel, SubnetFilterView> {}
}