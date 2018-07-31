package ru.protei.portal.ui.product.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemActivity;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;

/**
 * Вид карточки продукта
 */
public class ProductItemView extends Composite implements AbstractProductItemView {

    public ProductItemView() {
        initWidget (ourUiBinder.createAndBindUi (this));
    }

    public void setActivity(AbstractProductItemActivity activity) {
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
        this.name.setInnerText(name);
    }

    @Override
    public void setType(En_DevUnitType type) {
        typeImg.setUrl(type.getImgSrc());
        typeImg.setTitle(typeLang.getName(type));
        typeImg.setAltText(typeLang.getName(type));
    }

    @Override
    public void setDeprecated(boolean value) {
        if (value) {
            addStyleName( "inactive" );
            return;
        }

        removeStyleName( "inactive" );
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public void setEditEnabled( boolean isEnabled ) {
        if (isEnabled) {
            edit.removeStyleName( "link-disabled" );
        } else {
            edit.addStyleName( "link-disabled" );
        }
    }

    @UiField
    HeadingElement name;
    @UiField
    Image typeImg;
    @UiField
    Anchor edit;
    @UiField
    Anchor favorite;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    Anchor preview;

    @Inject
    En_DevUnitTypeLang typeLang;

    AbstractProductItemActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductItemView> {}
}