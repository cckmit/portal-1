package ru.protei.portal.ui.company.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;

/**
 * Представление компании
 */
public class CompanyItemView extends Composite implements AbstractCompanyItemView {

    public CompanyItemView() {
        initWidget( ourUiBinder.createAndBindUi ( this ) );
    }

    @Override
    public void setActivity( AbstractCompanyItemActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setName( String name ) {
        this.name.setInnerText( name );
    }

    @Override
    public void setType( En_CompanyCategory type ) {
        root.addStyleName( type.name().toLowerCase() );
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public void setPhone( String value ) {
        phoneContainer.setVisible( value != null && !value.isEmpty() );
        phone.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setEmail( String value ) {
        emailContainer.setVisible( value != null && !value.isEmpty() );
        email.setInnerHTML( value == null ? "" : value );
    }

    @Override
    public void setWebsite( String value ) {
        websiteContainer.setVisible( value != null && !value.isEmpty() );
        website.setInnerText( value == null ? "" : value );
        String href = value == null ? "#" : value;
        if ( !href.startsWith("http://") && !href.startsWith("htts://") ) {
            href = "http://" + href;
        }
        website.setHref( href );
    }

    @Override
    public void setEditEnabled( boolean isEnabled ) {
        if (isEnabled) {
            edit.removeStyleName( "link-disabled" );
        } else {
            edit.addStyleName( "link-disabled" );
        }
    }

    @UiHandler( "edit" )
    public void onEditClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onEditClicked( this );
        }
    }

    @UiHandler( "favorite" )
    public void onFavoriteClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onFavoriteClicked( this );
        }
    }

    @UiHandler( "preview" )
    public void onPreviewClicked ( ClickEvent event )
    {
        event.preventDefault();
        if (activity != null) {
            activity.onPreviewClicked( this );
        }
    }

    @UiField
    HeadingElement name;
    @UiField
    DivElement type;
    @UiField
    Anchor edit;
    @UiField
    Anchor favorite;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel root;
    @UiField
    SpanElement phone;
    @UiField
    SpanElement email;
    @UiField
    AnchorElement website;
    @UiField
    HTMLPanel phoneContainer;
    @UiField
    HTMLPanel emailContainer;
    @UiField
    HTMLPanel websiteContainer;

    AbstractCompanyItemActivity activity;

    interface CompanyItemViewUiBinder extends UiBinder<HTMLPanel, CompanyItemView> {}
    private static CompanyItemViewUiBinder ourUiBinder = GWT.create( CompanyItemViewUiBinder.class );

}