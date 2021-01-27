package ru.protei.portal.ui.common.client.view.projectfilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.projectfilter.AbstractProjectFilterActivity;
import ru.protei.portal.ui.common.client.activity.projectfilter.AbstractProjectFilterView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.selector.state.RegionStateBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Представление фильтра проектов
 */
public class ProjectFilterView extends Composite implements AbstractProjectFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        sortField.setType( ModuleType.PROJECT );
        fillDateRanges(commentCreationRange);
    }

    @Override
    public void setActivity( AbstractProjectFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public HasValue<Set<En_RegionState>> states() {
        return states;
    }

    @Override
    public HasValue<Set<ProductDirectionInfo>> direction() {
        return direction;
    }

    @Override
    public HasValue<Boolean> onlyMineProjects() {
        return onlyMineProjects;
    }

    @Override
    public HasValue< Set<EntityOption> > regions() {
        return regions;
    }

    @Override
    public HasValue<Set<PersonShortView>> headManagers() {
        return headManagers;
    }

    @Override
    public HasValue<Set<PersonShortView>> caseMembers() {
        return caseMembers;
    }

    @Override
    public HasValue<Set<EntityOption>> initiatorCompanies() {
        return initiatorCompanies;
    }

    @Override
    public HasValue<DateIntervalWithType> commentCreationRange() {
        return commentCreationRange;
    }

    @Override
    public boolean isCommentCreationRangeTypeValid() {
        return !commentCreationRange.isTypeMandatory()
                || (commentCreationRange.getValue() != null
                && commentCreationRange.getValue().getIntervalType() != null);
    }

    @Override
    public boolean isCommentCreationRangeValid() {
        return isDateRangeValid(commentCreationRange.getValue());
    }

    @Override
    public void setCommentCreationRangeValid(boolean isTypeValid, boolean isRangeValid) {
        commentCreationRange.setValid(isTypeValid, isRangeValid);
    }

    @Override
    public HasVisibility onlyMineProjectsVisibility() {
        return onlyMineProjectsContainer;
    }

    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.project_name );
        sortDir.setValue( true );
        search.setValue( "" );
        direction.setValue( new HashSet<>() );
        states.setValue( new HashSet<>() );
        regions.setValue(new HashSet<>());
        headManagers.setValue(new HashSet<>());
        caseMembers.setValue(new HashSet<>());
        onlyMineProjects.setValue( false );
        initiatorCompanies.setValue( null );
        commentCreationRange.setValue(null);
    }

    @Override
    public void clearFooterStyle() {
        footer.removeClassName("card-footer");
    }

    @Override
    public void fillStatesButtons(Map<En_RegionState, String> iconsColorsMap) {
        states.fillButtons(iconsColorsMap);
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent<En_SortField> event ) {
        if ( activity != null ) {
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler( "states" )
    public void onStateSelected( ValueChangeEvent<Set<En_RegionState>> event ) {
        if ( activity != null ) {
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler( "direction" )
    public void onDirectionSelected( ValueChangeEvent<Set<ProductDirectionInfo>> event ) {
        if ( activity != null ) {
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiHandler( "onlyMineProjects" )
    public void onOnlyMineProjectsChanged( ValueChangeEvent<Boolean> event ) {
        if (activity != null) {
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler( "regions" )
    public void onRegionSelected( ValueChangeEvent<Set<EntityOption>> event ) {
        if ( activity != null ) {
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler( "headManagers" )
    public void onHeadManagerSelected(ValueChangeEvent<Set<PersonShortView>> event ) {
        if ( activity != null ) {
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler( "caseMembers" )
    public void onCaseMemberSelected( ValueChangeEvent<Set<PersonShortView>> event ) {
        if ( activity != null ) {
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler( "initiatorCompanies" )
    public void onInitiatorCompaniesSelected( ValueChangeEvent<Set<EntityOption>> event ) {
        if ( activity != null ) {
            activity.onProjectFilterChanged();
        }
    }

    @UiHandler("commentCreationRange")
    public void onDateRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        if ( activity != null ) {
            activity.onProjectFilterChanged();
        }
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onProjectFilterChanged();
            }
        }
    };

    private void fillDateRanges(TypedSelectorRangePicker rangePicker) {
        rangePicker.fillSelector(En_DateIntervalType.issueTypes());
    }

    public boolean isDateRangeValid(DateIntervalWithType dateRange) {
        if (dateRange == null || dateRange.getIntervalType() == null) {
            return true;
        }

        return !Objects.equals(dateRange.getIntervalType(), En_DateIntervalType.FIXED) || dateRange.getInterval().isValid();
    }


    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    CleanableSearchBox search;

    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField( provided = true )
    RegionStateBtnGroupMulti states;

    @Inject
    @UiField(provided = true)
    RegionMultiSelector regions;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector headManagers;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector caseMembers;

    @Inject
    @UiField( provided = true )
    ProductDirectionMultiSelector direction;

    @Inject
    @UiField( provided = true )
    CompanyMultiSelector initiatorCompanies;

    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker commentCreationRange;

    @UiField
    HTMLPanel onlyMineProjectsContainer;
    @UiField
    CheckBox onlyMineProjects;
    @UiField
    DivElement footer;

    AbstractProjectFilterActivity activity;

    private static ProjectFilterView.RegionFilterViewUiBinder ourUiBinder = GWT.create( ProjectFilterView.RegionFilterViewUiBinder.class );
    interface RegionFilterViewUiBinder extends UiBinder<HTMLPanel, ProjectFilterView> {}

}