package ru.protei.portal.ui.common.client.widget.deliveryfilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryFilterType;
import ru.protei.portal.core.model.dto.DeliveryFilterDto;
import ru.protei.portal.core.model.ent.DeliveryFilter;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryFilterParamView;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryFilterWidgetModel;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.view.deliveryfilter.DeliveryFilterParamView;
import ru.protei.portal.ui.common.client.widget.deliveryfilterselector.DeliveryFilterSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.AsyncPersonModel;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

/**
 * Представление фильтра поставок
 */
public class DeliveryFilterWidget extends Composite {

    @Inject
    public void onInit(AbstractDeliveryFilterWidgetModel model) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.model = model;
        ensureDebugIds();
        deliveryFilterParamView.setCreatorModel(asyncPersonModel);
        deliveryFilterParamView.commentAuthorsVisibility().setVisible(false);
        deliveryFilterParamView.timeElapsedVisibility().setVisible(false);
    }

    public void setInitiatorCompaniesModel(AsyncSelectorModel companyModel) {
        deliveryFilterParamView.setInitiatorCompaniesModel(companyModel);
    }

    public void setManagerCompaniesModel(AsyncSelectorModel companyModel) {
        deliveryFilterParamView.setManagerCompaniesModel(companyModel);
    }

    public void resetFilter( DateIntervalWithType dateModified) {
        deliveryFilterParamView.resetFilter( dateModified );
        userFilter.setValue(null);
        removeBtn.setVisible(false);
        saveBtn.setVisible(false);
        createBtn.setVisible(true);
        filterName.removeStyleName(REQUIRED);
        filterName.setValue("");

        showUserFilterControls();
        applyVisibility(filterType);
    }

    public HasEnabled createEnabled() {
        return createBtn;
    }

    public AbstractDeliveryFilterParamView getDeliveryFilterParams() {
        return deliveryFilterParamView;
    }

    public void presetFilterType() {
        userFilter.updateFilterType(En_DeliveryFilterType.DELIVERY_OBJECTS);
    }

    public DeliveryQuery getFilterFieldsByFilterType() {
        return deliveryFilterParamView.getFilterFields(filterType);
    }

    public void clearFooterStyle() {
        footer.removeClassName("card-footer");
    }

    public HasValue<FilterShortView> userFilter() {
        return userFilter;
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

        DeliveryFilterDto<DeliveryQuery> filledUserFilter = fillUserFilter();
        if (!isCreateFilterAction) {
            filledUserFilter.getDeliveryFilter().setId(userFilter.getValue().getId());
        }
        model.onOkSavingFilterClicked(filterName.getValue(), filledUserFilter,
                deliveryFilterDto -> {
                    editBtnVisibility().setVisible(true);
                    removeFilterBtnVisibility().setVisible(true);
                    userFilter.setValue(deliveryFilterDto.getDeliveryFilter().toShortView());

                    showUserFilterControls();
                });
    }

    @UiHandler( "cancelBtn" )
    public void onCancelBtnClicked ( ClickEvent event ) {
        event.preventDefault();
        showUserFilterControls();
        if (userFilter.getValue() == null) {
            removeBtn.setVisible(false);
            saveBtn.setVisible(false);
        }
    }

    @UiHandler( "removeBtn" )
    public void onRemoveClicked ( ClickEvent event ) {
        FilterShortView value = userFilter.getValue();
        if (value == null || value.getId() == null) {
            return;
        }
        model.onRemoveClicked(value.getId(), () -> resetFilter(null) );
    }

    @UiHandler( "filterName" )
    public void onFilterNameChanged( KeyUpEvent event ) {
        filterNameChangedTimer.cancel();
        filterNameChangedTimer.schedule( 300 );
    }

    @UiHandler("userFilter")
    public void onKeyUpSearch(ValueChangeEvent<FilterShortView> event) {
        onUserFilterChanged(event.getValue());
    }

    private void onUserFilterChanged(FilterShortView filter) {
        if (filter == null){
            resetFilter(null);
            showUserFilterControls();

            return;
        }

        model.onUserFilterChanged(filter.getId(), deliveryFilterDto -> {
            deliveryFilterParamView.fillFilterFields(deliveryFilterDto.getQuery(), deliveryFilterDto.getDeliveryFilter().getSelectorsParams());
            filterName.setValue( deliveryFilterDto.getDeliveryFilter().getName() );
            removeFilterBtnVisibility().setVisible( true );
            editBtnVisibility().setVisible( true );
        });
    }

    private void showUserFilterName(){
        setUserFilterControlsVisibility(false);
        setUserFilterNameVisibility(true);
    }

    public void showUserFilterControls() {
        setUserFilterControlsVisibility(true);
        setUserFilterNameVisibility(false);
    }


    private DeliveryFilterDto<DeliveryQuery> fillUserFilter() {
        DeliveryFilter filter = new DeliveryFilter();
        filter.setName(filterName.getValue());
        filter.setType(filterType);
        DeliveryQuery query = deliveryFilterParamView.getFilterFields(filterType);
        query.setSearchString(deliveryFilterParamView.searchPattern().getValue());
        return new DeliveryFilterDto<>(filter, query);
    }

    private void ensureDebugIds() {
        userFilter.setEnsureDebugId(DebugIds.FILTER.USER_FILTER.FILTERS_BUTTON);
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

    public void updateFilterType(En_DeliveryFilterType filterType) {
        this.filterType = filterType;
        applyVisibility(filterType);
        resetFilter(null);
        userFilter.updateFilterType(filterType);
    }

    private void applyVisibility(En_DeliveryFilterType filterType) {
        deliveryFilterParamView.applyVisibility(filterType);
    }

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

    @Inject
    @UiField(provided = true)
    DeliveryFilterSelector userFilter;
    @Inject
    @UiField(provided = true)
    DeliveryFilterParamView deliveryFilterParamView;
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
    TextBox filterName;
    @UiField
    DivElement filterNameContainer;
    @UiField
    DivElement footer;

    @Inject
    AsyncPersonModel asyncPersonModel;

    AbstractDeliveryFilterWidgetModel model;

    private boolean isCreateFilterAction = true;
    private En_DeliveryFilterType filterType = En_DeliveryFilterType.DELIVERY_OBJECTS;
    private static DeliveryFilterWidget.DeliveryFilterViewUiBinder ourUiBinder = GWT.create( DeliveryFilterWidget.DeliveryFilterViewUiBinder.class );
    interface DeliveryFilterViewUiBinder extends UiBinder<HTMLPanel, DeliveryFilterWidget> {}
}
