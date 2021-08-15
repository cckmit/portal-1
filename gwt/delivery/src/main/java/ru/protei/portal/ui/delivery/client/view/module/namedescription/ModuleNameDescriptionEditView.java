package ru.protei.portal.ui.delivery.client.view.module.namedescription;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.markdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.dict.En_Privilege.DELIVERY_EDIT;
import static ru.protei.portal.core.model.dict.En_TextMarkup.MARKDOWN;
import static ru.protei.portal.core.model.util.CrmConstants.NAME_MAX_SIZE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HAS_ERROR;

public class ModuleNameDescriptionEditView extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        name.setMaxLength(NAME_MAX_SIZE);
        description.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( DESCRIPTION, isDisplay ) );
        description.setRenderer( ( text, consumer ) -> renderMarkupText( text, MARKDOWN, consumer ) );
        ensureDebugIds();
    }

    public HasValue<String> name() {
        return name;
    }

    public HasValue<String> description(){
        return description;
    }

    public HasWidgets getButtonContainer() {
        return buttonContainer;
    }

    private void renderMarkupText( String text, En_TextMarkup markup, Consumer<String> consumer ) {
        textRenderController.render( text, markup, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    public HasValidable getNameValidator() {
        return nameValidator;
    }

    private HasValidable nameValidator = new HasValidable() {
        @Override
        public void setValid( boolean isValid ) {
            if (isValid) {
                nameContainer.removeStyleName( HAS_ERROR );
            } else {
                nameContainer.addStyleName( HAS_ERROR );
            }
        }

        @Override
        public boolean isValid() {
            return HelperFunc.isNotEmpty( name.getValue() ) && name.isValid();
        }
    };

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        name.ensureDebugId( DebugIds.DELIVERY.KIT.MODULE.NAME );
        description.setEnsureDebugId( DebugIds.DELIVERY.KIT.MODULE.DESCRIPTION );
    }

    private void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( DELIVERY_EDIT + "_" + key, String.valueOf( isDisplay ) );
    }

    @UiField
    Lang lang;

    @UiField
    ValidableTextBox name;
    @UiField
    MarkdownAreaWithPreview description;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    HTMLPanel buttonContainer;

    @Inject
    TextRenderControllerAsync textRenderController;

    @Inject
    LocalStorageService localStorageService;

    private final String DESCRIPTION = "description";

    interface ModuleNameWidgetUiBinder extends UiBinder<HTMLPanel, ModuleNameDescriptionEditView> {}
    private static ModuleNameWidgetUiBinder ourUiBinder = GWT.create( ModuleNameWidgetUiBinder.class );
}
