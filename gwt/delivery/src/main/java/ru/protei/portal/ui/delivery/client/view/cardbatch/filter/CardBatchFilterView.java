package ru.protei.portal.ui.delivery.client.view.cardbatch.filter;

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
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.delivery.client.activity.card.filter.AbstractCardFilterActivity;
import ru.protei.portal.ui.delivery.client.activity.card.filter.AbstractCardFilterView;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.filter.AbstractCardBatchFilterActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.filter.AbstractCardBatchFilterView;
import ru.protei.portal.ui.delivery.client.widget.card.selector.CardTypeMultiSelector;
import ru.protei.portal.ui.delivery.client.widget.card.state.CardStatesOptionList;

import java.util.HashSet;
import java.util.Set;

/**
 * Представление фильтра плат
 */
public class CardBatchFilterView extends Composite implements AbstractCardBatchFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        resetFilter();
    }

    @Override
    public void setActivity( AbstractCardBatchFilterActivity activity ) {
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
//        sortField.setValue( En_SortField.card_serial_number );
        sortDir.setValue( true );
        search.setValue( "" );
        managers.setValue( new HashSet<>() );
        types.setValue( new HashSet<>() );
        states.setValue( new HashSet<>() );

/*      поиск по номеру партии, артикулу
        тип платы
        статус
        приоритет
        дедлайн
        исполнители

        Сортировка по типу платы + номеру партии в обратном порядке, дедлайну*/
    }

    @Override
    public HasValue<Set<EntityOption>> types() {
        return types;
    }

    @Override
    public HasValue<Set<CaseState>>  states() {
        return states;
    }

    @Override
    public HasValue<Set<PersonShortView>> managers() {
        return managers;
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event )  {
        fireChangeTimer();
    }

    @UiHandler( "managers" )
    public void onManagerSelected( ValueChangeEvent<Set<PersonShortView>> event ) {
        fireChangeTimer();
    }

    @UiHandler( "types" )
    public void onTypeSelected( ValueChangeEvent<Set<EntityOption>> event ) {
        fireChangeTimer();
    }

    @UiHandler( "states" )
    public void onStateSelected( ValueChangeEvent<Set<CaseState>> event ) {
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
    @UiField(provided = true)
    EmployeeMultiSelector managers;
    @Inject
    @UiField(provided = true)
    CardTypeMultiSelector types;
    @Inject
    @UiField(provided = true)
    CardStatesOptionList states;
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

    AbstractCardBatchFilterActivity activity;

    private static CardFilterViewUiBinder ourUiBinder = GWT.create( CardFilterViewUiBinder.class );
    interface CardFilterViewUiBinder extends UiBinder<HTMLPanel, CardBatchFilterView> {}
}