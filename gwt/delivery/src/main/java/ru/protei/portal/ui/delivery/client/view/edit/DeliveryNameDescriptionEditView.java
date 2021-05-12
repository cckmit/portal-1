package ru.protei.portal.ui.delivery.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.markdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.edit.AbstractDeliveryNameDescriptionEditActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.dict.En_Privilege.DELIVERY_EDIT;
import static ru.protei.portal.core.model.dict.En_TextMarkup.MARKDOWN;
import static ru.protei.portal.core.model.util.CrmConstants.NAME_MAX_SIZE;

public class DeliveryNameDescriptionEditView extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        name.setMaxLength(NAME_MAX_SIZE);
        description.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( DESCRIPTION, isDisplay ) );
        description.setRenderer( ( text, consumer ) -> renderMarkupText( text, MARKDOWN, consumer ) );
        ensureDebugIds();
    }

    public HasText name() {
        return name;
    }

    public HasValue<String> description(){
        return description;
    }

    public void setActivity( AbstractDeliveryNameDescriptionEditActivity activity ) {
        this.activity = activity;
    }

    @UiHandler("saveNameAndDescriptionButton")
    void onSaveNameAndDescriptionButtonClick( ClickEvent event ) {
        activity.saveIssueNameAndDescription();
    }

    @UiHandler("cancelNameAndDescriptionButton")
    void onCancelNameAndDescriptionButtonClick( ClickEvent event ) {
        activity.onNameDescriptionChanged();
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
        name.ensureDebugId( DebugIds.ISSUE.NAME_INPUT );
        description.setEnsureDebugId( DebugIds.ISSUE.DESCRIPTION_INPUT );
        nameLabel.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME );
        descriptionLabel.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO );
        saveNameAndDescriptionButton.ensureDebugId(DebugIds.ISSUE.EDIT_NAME_AND_DESC_ACCEPT);
        cancelNameAndDescriptionButton.ensureDebugId(DebugIds.ISSUE.EDIT_NAME_AND_DESC_REJECT);
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
    LabelElement descriptionLabel;
    @UiField
    HTMLPanel descriptionContainer;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    LabelElement nameLabel;
    @UiField
    Button saveNameAndDescriptionButton;
    @UiField
    Button cancelNameAndDescriptionButton;
    @UiField
    DivElement nameAndDescriptionButtonsPanel;

    @Inject
    TextRenderControllerAsync textRenderController;

    @Inject
    LocalStorageService localStorageService;

    public static final String HAS_ERROR = "has-error";
    private AbstractDeliveryNameDescriptionEditActivity activity;

    private List<Attachment> tempAttachment = new ArrayList<>();
    private final String DESCRIPTION = "description";

    interface IssueNameWidgetUiBinder extends UiBinder<HTMLPanel, DeliveryNameDescriptionEditView> {}
    private static IssueNameWidgetUiBinder ourUiBinder = GWT.create( IssueNameWidgetUiBinder.class );
}
