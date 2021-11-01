package ru.protei.portal.ui.delivery.client.view.pcborder.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.delivery.client.activity.pcborder.filter.AbstractPcbOrderFilterActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.filter.AbstractPcbOrderFilterView;

public class PcbOrderFilterView extends Composite implements AbstractPcbOrderFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        resetFilter();
    }

    @Override
    public void setActivity( AbstractPcbOrderFilterActivity activity ) {
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
        sortField.setValue( En_SortField.card_serial_number );
        sortDir.setValue( true );
        search.setValue( "" );
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event )  {
        fireChangeTimer();
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent<En_SortField> event )  {
        fireChangeTimer();
    }

    @UiHandler( "sortDir" )
    public void onSortDirClicked( ClickEvent event )  {
        fireChangeTimer();
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        resetFilter();
        if ( activity != null ) {
            activity.onFilterChanged();
        }
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

    AbstractPcbOrderFilterActivity activity;

    private static PcbOrderViewUiBinder ourUiBinder = GWT.create( PcbOrderViewUiBinder.class );
    interface PcbOrderViewUiBinder extends UiBinder<HTMLPanel, PcbOrderFilterView> {}
}