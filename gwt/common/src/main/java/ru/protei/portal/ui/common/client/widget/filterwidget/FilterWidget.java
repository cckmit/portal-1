package ru.protei.portal.ui.common.client.widget.filterwidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.filterwidget.Filter;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;
import ru.protei.portal.core.model.view.filterwidget.FilterShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

public abstract class FilterWidget<F extends Filter<FSV, Q>, Q extends FilterQuery, FSV extends FilterShortView> extends Composite {

    public void onInit(FilterWidgetModel<F, FSV> model) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.model = model;
        ensureDebugIds();
        filterSelector.addValueChangeHandler(event -> onUserFilterChanged(event.getValue()));
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        filterParamView.watchForScrollOf(root);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        filterParamView.stopWatchForScrollOf(root);
    }

    public void resetFilter() {
        filterParamView.resetFilter();
        filterSelector.setValue(null);
        removeBtn.setVisible(false);
        saveBtn.setVisible(false);
        createBtn.setVisible(true);
        filterName.removeStyleName(REQUIRED);
        filterName.setValue("");
    }

    public HasEnabled createEnabled() {
        return createBtn;
    }

    abstract protected FilterParamView<Q> getFilterParamView();

    public void clearFooterStyle() {
        footer.removeClassName("card-footer");
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        onUserFilterChanged(null);
    }

    @UiHandler( "saveBtn" )
    public void onSaveClicked ( ClickEvent event ) {
        isCreateFilterAction = false;
        showUserFilterName();
    }

    @UiHandler( "createBtn" )
    public void onCreateClicked ( ClickEvent event ) {
        isCreateFilterAction = true;
        showUserFilterName();
    }

    @UiHandler( "okBtn" )
    public void onOkBtnClicked ( ClickEvent event ) {
        event.preventDefault();
        if (isEmpty(filterName.getValue())) {
            setFilterNameContainerErrorStyle( true );
        }

        F filledUserFilter = fillUserFilter();
        if (!isCreateFilterAction) {
            filledUserFilter.setId(filterSelector.getValue().getId());
        }

        model.onOkSavingFilterClicked(filterName.getValue(), filledUserFilter,
                filter -> {
                    editBtnVisibility().setVisible(true);
                    removeFilterBtnVisibility().setVisible(true);
                    this.filterSelector.setValue(filter.toShortView());

                    showUserFilterControls();
                });
    }

    @UiHandler( "cancelBtn" )
    public void onCancelBtnClicked ( ClickEvent event ) {
        event.preventDefault();
        showUserFilterControls();
        if (filterSelector.getValue() == null) {
            removeBtn.setVisible(false);
            saveBtn.setVisible(false);
        }
    }

    @UiHandler( "removeBtn" )
    public void onRemoveClicked ( ClickEvent event ) {
        FilterShortView value = filterSelector.getValue();
        if (value == null || value.getId() == null) {
            return;
        }
        model.onRemoveClicked(value.getId(), this::resetFilter);
    }

    @UiHandler( "filterName" )
    public void onFilterNameChanged( KeyUpEvent event ) {
        filterNameChangedTimer.cancel();
        filterNameChangedTimer.schedule( 300 );
    }

    protected void onUserFilterChanged(FSV filterShortView) {
        if (filterShortView == null){
            resetFilter();
            showUserFilterControls();

            return;
        }

        model.onUserFilterChanged(filterShortView.getId(), filterAfter -> {
            filterParamView.fillFilterFields(filterAfter.getQuery(), filterAfter.getSelectorsParams());
            fillFilterAfter();
            filterName.setValue( filterAfter.getName() );
            removeFilterBtnVisibility().setVisible( true );
            editBtnVisibility().setVisible( true );
        });
    }

    protected void fillFilterAfter() {};

    private void showUserFilterName(){
        setUserFilterControlsVisibility(false);
        setUserFilterNameVisibility(true);
    }

    public void showUserFilterControls() {
        setUserFilterControlsVisibility(true);
        setUserFilterNameVisibility(false);
    }

    abstract protected F fillUserFilter();

    private void ensureDebugIds() {
        filterSelector.setEnsureDebugId(DebugIds.FILTER.USER_FILTER.FILTERS_BUTTON);
        filterName.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_NAME_INPUT);
        okBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_OK_BUTTON);
        cancelBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_CANCEL_BUTTON);
        createBtn.ensureDebugId(DebugIds.FILTER.CREATE_BUTTON);
        saveBtn.ensureDebugId(DebugIds.FILTER.SAVE_BUTTON);
        resetBtn.ensureDebugId(DebugIds.FILTER.RESET_BUTTON);
        removeBtn.ensureDebugId(DebugIds.FILTER.REMOVE_BUTTON);
    }

    Timer filterNameChangedTimer = new Timer() {
        @Override
        public void run() {
            setFilterNameContainerErrorStyle( filterName.getValue().isEmpty() );
        }
    };

    private HasVisibility removeFilterBtnVisibility(){
        return removeBtn;
    }

    private void setFilterNameContainerErrorStyle( boolean hasError ) {
        if ( hasError ) {
            filterName.addStyleName(REQUIRED);
        } else {
            filterName.removeStyleName( REQUIRED );
        }
    }

    private void setUserFilterNameVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            filterNameContainer.removeClassName( HIDE );
        } else {
            filterNameContainer.addClassName( HIDE );
        }
    }

    private void setUserFilterControlsVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            createBtn.removeStyleName( HIDE );
            saveBtn.removeStyleName( HIDE );
            resetBtn.removeStyleName( HIDE );
            removeBtn.removeStyleName( HIDE );
        } else {
            saveBtn.addStyleName( HIDE );
            createBtn.addStyleName( HIDE );
            resetBtn.addStyleName( HIDE );
            removeBtn.addStyleName( HIDE );
        }
    }

    private HasVisibility editBtnVisibility() {
        return saveBtn;
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel root;

    @UiField(provided = true)
    protected FilterSelector<FSV> filterSelector;
    @UiField(provided = true)
    protected FilterParamView<Q> filterParamView;

    @UiField
    Button resetBtn;
    @UiField
    Button createBtn;
    @UiField
    Button saveBtn;
    @UiField
    Button removeBtn;
    @UiField
    Anchor okBtn;
    @UiField
    Anchor cancelBtn;
    @UiField
    public TextBox filterName;
    @UiField
    DivElement filterNameContainer;
    @UiField
    DivElement footer;

    FilterWidgetModel<F, FSV> model;

    private boolean isCreateFilterAction = true;
    private static FilterWidgetViewUiBinder ourUiBinder = GWT.create( FilterWidgetViewUiBinder.class );
    interface FilterWidgetViewUiBinder extends UiBinder<HTMLPanel, FilterWidget<?, ?, ?>> {}
}
