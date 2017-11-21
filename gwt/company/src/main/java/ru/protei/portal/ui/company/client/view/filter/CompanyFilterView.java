package ru.protei.portal.ui.company.client.view.filter;

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
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterActivity;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterView;
import ru.protei.portal.ui.company.client.widget.category.btngroup.CategoryBtnGroupMulti;
import ru.protei.portal.ui.company.client.widget.group.buttonselector.GroupButtonSelector;

import java.util.Set;

/**
 * Представление фильтра контактов
 */
public class CompanyFilterView extends Composite implements AbstractCompanyFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        group.setDefaultValue( lang.selectCompanyGroup() );
        search.getElement().setPropertyString( "placeholder", lang.search() );
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
    public void setActivity( AbstractCompanyFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue< EntityOption > group() {
        return group;
    }

    @Override
    public HasValue<Set< EntityOption >> categories() {
        return categories;
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

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @Override
    public void resetFilter() {
        categories.setValue(null);
        group.setValue(null);
        sortField.setValue( En_SortField.comp_name );
        sortDir.setValue(true);
        search.setText( "" );
    }

    @UiHandler( "categories" )
    public void onCompanyCategorySelected( ValueChangeEvent< Set< EntityOption> > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "group" )
    public void onCompanyGroupSelected( ValueChangeEvent< EntityOption > event ) {
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
    GroupButtonSelector group;

    @Inject
    @UiField( provided = true )
    CategoryBtnGroupMulti categories;

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
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;


    AbstractCompanyFilterActivity activity;

    private static CompanyFilterView.CompanyFilterViewUiBinder ourUiBinder = GWT.create( CompanyFilterView.CompanyFilterViewUiBinder.class );
    interface CompanyFilterViewUiBinder extends UiBinder<HTMLPanel, CompanyFilterView > {}

}