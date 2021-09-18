package ru.protei.portal.ui.delivery.client.view.delivery.namedescription;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

public class DeliveryNameDescriptionView extends Composite  {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    public void setName( String name ) {
        nameRO.setInnerHTML(name);
    }

    public void setDescription( String description ) {
        descriptionRO.setInnerHTML(description);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        nameRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DELIVERY.NAME);
        descriptionRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DELIVERY.DESCRIPTION);
    }

    @UiField
    Lang lang;

    @UiField
    LabelElement nameRO;
    @UiField
    DivElement descriptionRO;

    interface DeliveryNameAndDescriptionViewUiBinder extends UiBinder<HTMLPanel, DeliveryNameDescriptionView> { }
    private static DeliveryNameAndDescriptionViewUiBinder ourUiBinder = GWT.create( DeliveryNameAndDescriptionViewUiBinder.class );
}