package ru.protei.portal.ui.delivery.client.view.card.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.markdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.card.create.AbstractCardCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.card.create.AbstractCardCreateView;
import ru.protei.portal.ui.delivery.client.view.card.meta.CardMetaView;

import java.util.Date;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.dict.En_TextMarkup.MARKDOWN;


public class CardCreateView extends Composite implements AbstractCardCreateView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        note.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( NOTE, isDisplay ) );
        note.setRenderer(this::renderMarkupText);
        comment.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( COMMENT, isDisplay ) );
        comment.setRenderer(this::renderMarkupText);
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCardCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasValue<String> serialNumber() {
        return serialNumber;
    }

    @Override
    public HasValue<String> note() {
        return note;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasValue<CaseState> state() {
        return meta.state();
    }

    @Override
    public HasValue<EntityOption> type() {
        return meta.type();
    }

    @Override
    public HasValue<String> article() {
        return meta.article();
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return meta.manager();
    }

    @Override
    public HasValue<Date> testDate() {
        return meta.testDate();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        // todo fill
        saveButton.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.SAVE_BUTTON);
//        note.setEnsureDebugId( DebugIds.DELIVERY.KIT.MODULE.DESCRIPTION );
//        comment.setEnsureDebugId( DebugIds.DELIVERY.KIT.MODULE.DESCRIPTION );
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
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
    TextBox serialNumber;
    @Inject
    @UiField(provided = true)
    MarkdownAreaWithPreview note;
    @Inject
    @UiField(provided = true)
    MarkdownAreaWithPreview comment;
    @Inject
    @UiField(provided = true)
    CardMetaView meta;
    @UiField
    Button saveButton;

    @Inject
    LocalStorageService localStorageService;
    @Inject
    TextRenderControllerAsync textRenderController;

    private AbstractCardCreateActivity activity;

    static private final String NOTE = "Note";
    static private final String COMMENT = "Comment";

    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
    interface ViewUiBinder extends UiBinder<HTMLPanel, CardCreateView> {}
}
