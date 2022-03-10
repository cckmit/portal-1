package ru.protei.portal.ui.delivery.client.view.delivery.module.namedescription;

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

public class ModuleNameDescriptionView extends Composite  {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    public void setName( String name ) {
        this.name.setInnerHTML(name);
    }

    public void setDescription( String description ) {
        this.description.setInnerHTML(description);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        name.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DELIVERY.KIT.MODULE.NAME);
        description.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DELIVERY.KIT.MODULE.DESCRIPTION);
    }

    @UiField
    Lang lang;

    @UiField
    LabelElement name;
    @UiField
    DivElement description;

    interface ModuleNameAndDescriptionViewUiBinder extends UiBinder<HTMLPanel, ModuleNameDescriptionView> { }
    private static ModuleNameAndDescriptionViewUiBinder ourUiBinder = GWT.create( ModuleNameAndDescriptionViewUiBinder.class );
}