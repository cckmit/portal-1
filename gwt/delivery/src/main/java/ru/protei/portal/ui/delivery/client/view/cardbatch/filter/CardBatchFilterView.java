package ru.protei.portal.ui.delivery.client.view.cardbatch.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.card.type.CardTypeOptionMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.filter.AbstractCardBatchFilterActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.filter.AbstractCardBatchFilterView;
import ru.protei.portal.ui.delivery.client.widget.cardbatch.importance.CardBatchImportanceBtnGroupMulti;
import ru.protei.portal.ui.delivery.client.widget.cardbatch.state.CardBatchStatesOptionList;

import java.util.HashSet;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Представление фильтра партий плат
 */
public class CardBatchFilterView extends Composite implements AbstractCardBatchFilterView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        contractors.setPersonModel(contractorModel);
        deadline.fillSelector(En_DateIntervalType.defaultTypes());
        resetFilter();
    }

    @Override
    public void setActivity(AbstractCardBatchFilterActivity activity) {
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
        sortField.setValue( En_SortField.card_batch_type );
        sortDir.setValue( true );
        search.setValue( "" );
        contractors.setValue( new HashSet<>() );
        types.setValue( new HashSet<>() );
        states.setValue( new HashSet<>() );
        importance.setValue( null );
        deadline.setValue( null );
    }

    @Override
    public HasValue<Set<EntityOption>> cardTypes() {
        return types;
    }

    @Override
    public HasValue<Set<CaseState>> states() {
        return states;
    }

    @Override
    public HasValue<Set<PersonShortView>> contractors() {
        return contractors;
    }

    @Override
    public HasValue<DateIntervalWithType> deadline() {
        return deadline;
    }

    @Override
    public HasValue<Set<ImportanceLevel>> importance() {
        return importance;
    }

    @Override
    public void setContractorFilter(Long companyId) {
        contractorModel.updateCompanies(null, setOf(companyId));
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event )  {
        fireChangeTimer();
    }

    @UiHandler( "contractors" )
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

    @UiHandler("deadline")
    public void onDateSigningRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        fireChangeTimer();
    }

    @UiHandler("importance")
    public void onImportanceSelected(ValueChangeEvent<Set<ImportanceLevel>> event) {
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


    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        search.setDebugIdTextBox(DebugIds.FILTER.CARD_BATCH_SEARCH_BY_NUMBER_AND_ARTICLE_INPUT);
        contractors.ensureDebugId(DebugIds.FILTER.CARD_BATCH_CONTRACTORS_SELECTOR);
        contractors.setAddEnsureDebugId(DebugIds.FILTER.CARD_BATCH_CONTRACTORS_ADD_BUTTON);
        contractors.setClearEnsureDebugId(DebugIds.FILTER.CARD_BATCH_CONTRACTORS_CLEAR_BUTTON);
        contractors.setItemContainerEnsureDebugId(DebugIds.FILTER.CARD_BATCH_CONTRACTORS_ITEM_CONTAINER);

        types.ensureDebugId(DebugIds.FILTER.CARD_BATCH_CARD_TYPE_SELECTOR);
        types.setAddEnsureDebugId(DebugIds.FILTER.CARD_BATCH_CARD_TYPE_ADD_BUTTON);
        types.setClearEnsureDebugId(DebugIds.FILTER.CARD_BATCH_CARD_TYPE_CLEAR_BUTTON);
        types.setItemContainerEnsureDebugId(DebugIds.FILTER.CARD_BATCH_CARD_TYPE_ITEM_CONTAINER);

        deadline.ensureDebugId(DebugIds.FILTER.CARD_BATCH_DEADLINE_SELECTOR);
        sortField.ensureDebugId(DebugIds.FILTER.CARD_BATCH_SORT_FIELD);
        sortDir.ensureDebugId(DebugIds.FILTER.CARD_BATCH_SORT_DIRECTION);

        states.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.FILTER.CARD_BATCH_STATE_SELECTOR);
        importance.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.FILTER.CARD_BATCH_IMPORTANCE_SELECTOR);
        resetBtn.ensureDebugId(DebugIds.FILTER.RESET_BUTTON);
    }

    @UiField
    CleanableSearchBox search;
    @Inject
    @UiField(provided = true)
    PersonMultiSelector contractors;
    @Inject
    @UiField(provided = true)
    CardTypeOptionMultiSelector types;
    @Inject
    @UiField(provided = true)
    CardBatchStatesOptionList states;
    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker deadline;
    @Inject
    @UiField(provided = true)
    CardBatchImportanceBtnGroupMulti importance;
    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;

    @Inject
    PersonModel contractorModel;

    AbstractCardBatchFilterActivity activity;

    private static CardFilterViewUiBinder ourUiBinder = GWT.create( CardFilterViewUiBinder.class );
    interface CardFilterViewUiBinder extends UiBinder<HTMLPanel, CardBatchFilterView> {}
}