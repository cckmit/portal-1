package ru.protei.portal.ui.company.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListView;

/**
 * Вид формы списка компаний
 */
public class CompanyListView extends Composite implements AbstractCompanyListView {

    public CompanyListView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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

    @UiHandler( "searchButton" )
    public void onSearchClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSearchClicked();
        }
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
    Button searchButton;
    @UiField
    Button directionButton;
    @UiField
    ListBox groupList;
    @UiField
    ListBox sortList;

    AbstractCompanyListActivity activity;

    private static CompanyViewUiBinder ourUiBinder = GWT.create( CompanyViewUiBinder.class );
    interface CompanyViewUiBinder extends UiBinder<HTMLPanel, CompanyListView> {}

}