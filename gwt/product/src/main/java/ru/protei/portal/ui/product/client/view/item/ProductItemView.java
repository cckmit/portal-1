package ru.protei.portal.ui.product.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
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


    @UiHandler( "update" )
    public void onUpdateClicked ( ClickEvent event )
    {
        if (activity != null) {
            activity.onUpdateClicked( this );
        }
    }

    @Override
    public void setName(String name) {
        this.name.setInnerText(name);
    }

    @Override
    public void setDeprecated(boolean value) {
        if (value)
            this.getElement().getFirstChildElement().addClassName("inactive");
    }


    @UiField
    DivElement name;
    @UiField
    CheckBox update;

    AbstractProductItemActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductItemView> {}
}