package ru.protei.portal.ui.company.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListView;

/**
 * Вид формы списка компаний
 */
public class CompanyListView extends Composite implements AbstractCompanyListView, KeyUpHandler {

    public CompanyListView() {
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
    @UiField
    ListBox sortList;

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
    interface CompanyViewUiBinder extends UiBinder<HTMLPanel, CompanyListView> {}

}