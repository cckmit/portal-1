package ru.protei.portal.ui.delivery.client.view.card.infoComment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.markdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.dict.En_TextMarkup.MARKDOWN;

public class CardNoteCommentEditView extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        note.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( NOTE, isDisplay ) );
        note.setRenderer(this::renderMarkupText);
        comment.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( COMMENT, isDisplay ) );
        comment.setRenderer(this::renderMarkupText);
        ensureDebugIds();
    }

    public HasValue<String> note() {
        return note;
    }

    public HasValue<String> comment(){
        return comment;
    }

    public HasWidgets getButtonContainer() {
        return buttonContainer;
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        note.ensureDebugId( DebugIds.CARD.NOTE );
        comment.setEnsureDebugId( DebugIds.CARD.COMMENT );
    }

    private void renderMarkupText(String text, Consumer<String> consumer ) {
        textRenderController.render( text, MARKDOWN, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    private void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( this.getClass().getSimpleName() + "_" + key, String.valueOf( isDisplay ) );
    }

    @UiField
    Lang lang;
    @UiField
    MarkdownAreaWithPreview note;
    @UiField
    MarkdownAreaWithPreview comment;
    @UiField
    HTMLPanel buttonContainer;

    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;

    static private final String NOTE = "Note";
    static private final String COMMENT = "Comment";

    interface WidgetUiBinder extends UiBinder<HTMLPanel, CardNoteCommentEditView> {}
    private static WidgetUiBinder ourUiBinder = GWT.create( WidgetUiBinder.class );
}
