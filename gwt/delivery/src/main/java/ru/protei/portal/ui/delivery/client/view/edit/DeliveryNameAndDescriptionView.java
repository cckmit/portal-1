package ru.protei.portal.ui.delivery.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

public class DeliveryNameAndDescriptionView extends Composite  {

    public DeliveryNameAndDescriptionView() {
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

        nameRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.NAME_FIELD);
        nameRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.NAME_FIELD);
    }

    @UiField
    Lang lang;

    @UiField
    LabelElement nameRO;
    @UiField
    LabelElement descriptionRO;

    interface DeliveryNameAndDescriptionViewUiBinder extends UiBinder<HTMLPanel, DeliveryNameAndDescriptionView> { }
    private static DeliveryNameAndDescriptionViewUiBinder ourUiBinder = GWT.create( DeliveryNameAndDescriptionViewUiBinder.class );
}