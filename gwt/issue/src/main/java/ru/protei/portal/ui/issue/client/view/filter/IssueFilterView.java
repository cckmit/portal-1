package ru.protei.portal.ui.issue.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.issue.client.widget.importance.btngroup.ImportanceBtnGroupMulti;
import ru.protei.portal.ui.issue.client.widget.state.option.IssueStatesOptionList;

import java.util.Set;

/**
 * Представление фильтра обращений
 */
public class IssueFilterView extends Composite implements AbstractIssueFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        search.getElement().setPropertyString( "placeholder", lang.search() );
        sortField.setType( ModuleType.ISSUE );
        sortDir.setValue( false );
        company.setDefaultValue( lang.selectIssueCompany() );
        product.setDefaultValue( lang.selectIssueProduct() );
        manager.setDefaultValue( lang.selectIssueManager() );
        dateRange.setPlaceholder( lang.selectDate() );
   }


    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    @Override
    public void setActivity( AbstractIssueFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<ProductShortView> product() {
        return product;
    }

    @Override
    public HasValue<PersonShortView> manager () { return manager; }

    @Override
    public HasValue< Set <En_CaseState > > states() { return state; }

    @Override
    public HasValue< Set <En_ImportanceLevel> > importances() { return importance; }

    @Override
    public HasValue<DateInterval> dateRange() { return dateRange; }

    @Override
    public HasValue<En_SortField> sortField() { return sortField; }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public void resetFilter() {
        company.setValue( null );
        product.setValue( null );
        manager.setValue( null );
        importance.setValue(null);
        state.setValue( null );
        dateRange.setValue( null );
        sortField.setValue( En_SortField.creation_date );
        sortDir.setValue( false );
        search.setText( "" );
    }

    @Override
    public HasEnabled companyEnabled() {
        return company;
    }

    @Override
    public HasEnabled productEnabled() {
        return product;
    }

    @Override
    public HasEnabled managerEnabled() {
        return manager;
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "company" )
    public void onCompanySelected( ValueChangeEvent<EntityOption> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "product" )
    public void onProductSelected( ValueChangeEvent<ProductShortView> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "manager" )
    public void onManagerSelected( ValueChangeEvent<PersonShortView> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "importance" )
    public void onImportanceSelected( ValueChangeEvent<Set<En_ImportanceLevel>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "state" )
    public void onStateSelected( ValueChangeEvent<Set<En_CaseState>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "dateRange" )
    public void onDateRangeChanged( ValueChangeEvent<DateInterval> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }


    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
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
    public void onKeyUpSearch( KeyUpEvent event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    @Inject
    @UiField( provided = true )
    CompanySelector company;

    @Inject
    @UiField ( provided = true )
    ProductButtonSelector product;

    @Inject
    @UiField ( provided = true )
    EmployeeButtonSelector manager;

    @Inject
    @UiField ( provided = true )
    IssueStatesOptionList state;

    @Inject
    @UiField( provided = true )
    ImportanceBtnGroupMulti importance;

    @Inject
    @UiField(provided = true)
    RangePicker dateRange;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    TextBox search;

    @UiField
    Button resetBtn;

    @Inject
    FixedPositioner positioner;

    @Inject
    @UiField
    Lang lang;

    AbstractIssueFilterActivity activity;

    private static IssueFilterView.IssueFilterViewUiBinder ourUiBinder = GWT.create( IssueFilterView.IssueFilterViewUiBinder.class );
    interface IssueFilterViewUiBinder extends UiBinder<HTMLPanel, IssueFilterView > {}
}