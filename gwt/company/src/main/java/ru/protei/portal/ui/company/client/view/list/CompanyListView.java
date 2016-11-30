package ru.protei.portal.ui.company.client.view.list;

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
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.common.client.widget.platelist.events.AddEvent;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.togglebtn.item.*;
import ru.protei.portal.ui.common.client.widget.togglebtn.item.ToggleButton;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListView;
import ru.protei.portal.ui.company.client.widget.category.btngroup.CategoryBtnGroup;
import ru.protei.portal.ui.company.client.widget.group.buttonselector.GroupButtonSelector;

import java.util.Set;

/**
 * Представление списка компаний
 */
public class CompanyListView extends Composite implements AbstractCompanyListView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        search.getElement().setPropertyString( "placeholder", lang.search() );
        group.setDefaultValue(lang.companyGroup());
    }

    public void setActivity( AbstractCompanyListActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @Override
    public HasValue< String > searchPattern() {
        return search;
    }

    @Override
    public HasValue< En_SortField > sortField() {
        return sortField;
    }

    @Override
    public HasValue< CompanyGroup > group() {
        return group;
    }

    @Override
    public HasValue< Set < CompanyCategory > > categories() {
        return categories;
    }

    @Override
    public HasValue< Boolean > sortDir() {
        return sortDir;
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
    public void onCompanyCategorySelected( ValueChangeEvent< Set< CompanyCategory> > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "group" )
    public void onCompanyGroupSelected( ValueChangeEvent< CompanyGroup > event ) {
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
    public void onDirectionClicked( ValueChangeEvent<Boolean> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onKeyUpSearch( KeyUpEvent event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiHandler( "childContainer" )
    public void onAddClicked( AddEvent event ) {
        if ( activity != null ) {
            activity.onCreateClicked();
        }
    }

    @UiField
    TextBox search;

    @UiField
    PlateList childContainer;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @Inject
    @UiField( provided = true )
    GroupButtonSelector group;

    @Inject
    @UiField( provided = true )
    CategoryBtnGroup categories;

    @Inject
    @UiField
    Lang lang;
    @UiField
    ToggleButton sortDir;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    AbstractCompanyListActivity activity;

    private static CompanyViewUiBinder ourUiBinder = GWT.create( CompanyViewUiBinder.class );
    interface CompanyViewUiBinder extends UiBinder< HTMLPanel, CompanyListView > {}
}