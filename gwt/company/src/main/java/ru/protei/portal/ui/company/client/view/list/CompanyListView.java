package ru.protei.portal.ui.company.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListView;
import ru.protei.portal.ui.company.client.widget.selector.SortFieldSelector;

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
    public ListBox getGroupList() {
        return groupList;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @UiHandler( "customersButton" )
    public void onCustomersClicked( ClickEvent event ) {
    }

    @UiHandler( "partnersButton" )
    public void onPartnersClicked( ClickEvent event ) {
    }

    @Override
    public void onKeyUp( KeyUpEvent keyUpEvent ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    private void initHandlers() {
        search.sinkEvents( Event.ONKEYUP );
        search.addHandler( this, KeyUpEvent.getType() );
    }

    @UiField
    TextBox search;
    @UiField
    Button customersButton;
    @UiField
    Button partnersButton;
    @UiField
    HTMLPanel companyContainer;
    @UiField
    Button directionButton;
    @UiField
    ListBox groupList;
    //@UiField
    //ListBox sortList;

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
    SortFieldSelector sortField;

    AbstractCompanyListActivity activity;

    private static CompanyViewUiBinder ourUiBinder = GWT.create( CompanyViewUiBinder.class );
    interface CompanyViewUiBinder extends UiBinder<HTMLPanel, CompanyListView> {}

}