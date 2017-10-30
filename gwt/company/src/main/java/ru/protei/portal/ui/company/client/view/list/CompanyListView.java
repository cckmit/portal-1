package ru.protei.portal.ui.company.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyGridActivity;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListView;

/**
 * Представление списка компаний
 */
public class CompanyListView extends Composite implements AbstractCompanyListView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setActivity( AbstractCompanyGridActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @Override
    public void setListCreateBtnVisible( boolean isVisible ) {
        childContainer.setCreateButtonVisible( isVisible );
    }

    @UiHandler( "childContainer" )
    public void onAddClicked( AddEvent event ) {
        if ( activity != null ) {
            activity.onCreateClicked();
        }
    }

    @UiField
    PlateList childContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    AbstractCompanyGridActivity activity;

    private static CompanyViewUiBinder ourUiBinder = GWT.create( CompanyViewUiBinder.class );
    interface CompanyViewUiBinder extends UiBinder< HTMLPanel, CompanyListView > {}
}