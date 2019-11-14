package ru.protei.portal.ui.region.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
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

    @Override
    public void setName(String name) {
        this.name.setInnerText( name );
    }

    @Override
    public void setNumber( Integer number ) {
        this.number.setInnerText( number == null ? "" : number.toString() );
    }

    @UiField
    SpanElement name;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    SpanElement number;

    AbstractRegionItemActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, RegionItemView > {}
}