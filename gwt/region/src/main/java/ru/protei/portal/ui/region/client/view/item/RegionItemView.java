package ru.protei.portal.ui.region.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.ui.region.client.activity.item.AbstractRegionItemActivity;
import ru.protei.portal.ui.region.client.activity.item.AbstractRegionItemView;

/**
 * Вид карточки региона
 */
public class RegionItemView extends Composite implements AbstractRegionItemView {

    public RegionItemView() {
        initWidget (ourUiBinder.createAndBindUi (this));
    }

    public void setActivity(AbstractRegionItemActivity activity) {
        this.activity = activity;
    }

    @UiHandler( "favorite" )
    public void onFavoriteClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onFavoriteClicked( this );
        }
    }

    @UiHandler( "edit" )
    public void onEditClicked ( ClickEvent event )
    {
        event.preventDefault();
        if (activity != null) {
            activity.onEditClicked( this );
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

    @Override
    public void setName(String name) {
        this.name.setInnerText( name );
    }

    @Override
    public void setNumber( Integer number ) {
        this.number.setInnerText( number == null ? "" : number.toString() );
    }

    @Override
    public void setDetails( String details ) {
        this.details.setInnerText( details == null ? "" : details.toString() );
    }

    @Override
    public void setState( String value ) {
        state.setClassName( value );
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @UiField
    SpanElement name;
    @UiField
    Anchor edit;
    @UiField
    Anchor favorite;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    Anchor preview;
    @UiField
    SpanElement number;
    @UiField
    DivElement details;
    @UiField
    Element state;

    AbstractRegionItemActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, RegionItemView > {}
}