package ru.protei.portal.ui.company.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListView;
import ru.protei.portal.ui.company.client.widget.companycategorybtngroup.CompanyCategoryBtnGroup;
import ru.protei.portal.ui.company.client.widget.companygroupselector.CompanyGroupSelector;
import ru.protei.portal.ui.company.client.widget.sortfieldselector.SortFieldSelector;

import java.util.Set;

/**
 * Вид формы списка компаний
 */
public class CompanyListView extends Composite implements AbstractCompanyListView, KeyUpHandler {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initHandlers();
    }

    public void setActivity( AbstractCompanyListActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getCompanyContainer() {
        return companyContainer;
    }

    @Override
    public String getSearchPattern() {
        return search.getText();
    }

    @Override
    public HasValue< En_SortField > getSortField() {
        return sortField;
    }

    @Override
    public HasValue< CompanyGroup > getCompanyGroup() {
        return companyGroup;
    }

    @Override
    public HasValue< Set < CompanyCategory > > getCompanyCategory() {
        return companyCategory;
    }

    @Override
    public Boolean getDirSort() {
        return directionButton.getValue();
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "companyGroup" )
    public void onCompanyGroupSelected( ValueChangeEvent< CompanyGroup > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "companyCategory" )
    public void onCompanyCategorySelected( ValueChangeEvent< Set< CompanyCategory> > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "directionButton" )
    public void onDirectionClicked( ClickEvent event ) {

        if (directionButton.getValue())
            directionButton.removeStyleName( "active" );
        else
            directionButton.addStyleName( "active" );

        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @Override
    public void onKeyUp( KeyUpEvent keyUpEvent ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    public void resetFilter() {
        companyCategory.getValue().clear();
        companyGroup.setValue( null );
        sortField.setValue( En_SortField.comp_name );
        directionButton.setValue( true );
        search.setText( "" );
    }

    private void initHandlers() {
        search.sinkEvents( Event.ONKEYUP );
        search.addHandler( this, KeyUpEvent.getType() );
    }

    @UiField
    TextBox search;

    @UiField
    HTMLPanel companyContainer;

    @UiField
    ToggleButton directionButton;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @Inject
    @UiField( provided = true )
    CompanyGroupSelector companyGroup;

    @Inject
    @UiField( provided = true )
    CompanyCategoryBtnGroup companyCategory;

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