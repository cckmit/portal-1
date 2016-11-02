package ru.protei.portal.ui.company.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PushButton;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemActivity;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;

/**
 * Представление компании
 */
public class CompanyItemView extends Composite implements AbstractCompanyItemView {

    private static CompanyItemViewUiBinder ourUiBinder = GWT.create( CompanyItemViewUiBinder.class );

    public void setActivity( AbstractCompanyItemActivity activity ) {
        this.activity = activity;
    }

    public void setName( String name ) {
        this.name.setInnerText( name );
    }

    public void setType( String type ) {
        this.type.setInnerText( type );
    }

    @UiHandler( "menuButton" )
    public void onMenuClicked( ClickEvent event ) {

        if ( activity != null )
            activity.onMenuClicked( this );
    }

    @UiHandler( "favoriteButton" )
    public void onFavoriteClicked( ClickEvent event ) {
        if ( activity != null )
            activity.onFavoriteClicked( this );
    }

    @UiField
    DivElement name;
    @UiField
    DivElement type;
    @UiField
    PushButton menuButton;
    @UiField
    PushButton favoriteButton;

    AbstractCompanyItemActivity activity;

    interface CompanyItemViewUiBinder extends UiBinder<HTMLPanel, CompanyItemView> {}
    public CompanyItemView() {
        initWidget( ourUiBinder.createAndBindUi ( this ) );
    }
}