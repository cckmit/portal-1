package ru.protei.portal.ui.common.client.widget.markdown;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ToggleButton;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.dndautoresizetextarea.DndAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.imagepastetextarea.event.PasteEvent;
import ru.protei.portal.ui.common.client.widget.uploader.AbstractAttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.PasteInfo;
import ru.protei.portal.ui.common.shared.model.HTMLRenderer;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public class MarkdownAreaWithPreview
        extends Composite
        implements HasValue<String> {

    public MarkdownAreaWithPreview() {
        initWidget( ourUiBinder.createAndBindUi( this ) );

        text.getElement().setAttribute("placeholder", lang.markupPlaceholder());
        text.setOverlayText(lang.dropFilesHere());
    }

    private DisplayPreviewHandler displayPreviewHandler;

    public interface DisplayPreviewHandler {
        void onDisplayPreviewChanged( boolean isDisplay );
    }

    @Override
    public String getValue() {
        return text.getValue();
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvent) {
        text.setValue(value);
        previewChanged();
        if ( fireEvent ) {
            ValueChangeEvent.fire(this, text.getValue());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    public void setFileUploader(AbstractAttachmentUploader fileUploader) {
        this.fileUploader = fileUploader;
    }

    public void setDisplayPreview( boolean isDisplayPreview ) {
        this.isDisplayPreview.setValue(  isDisplayPreview );
    }

    public void setDisplayPreviewHandler( DisplayPreviewHandler displayPreviewHandler ) {
        this.displayPreviewHandler = displayPreviewHandler;
    }

    public void setRenderer( HTMLRenderer renderer) {
        this.renderer = renderer;
    }

    public void setMinRows(int rows) {
        text.setMinRows(rows);
    }

    public void setMaxRows(int rows) {
        text.setMaxRows(rows);
    }

    public void setExtraRows(int rows) {
        text.setExtraRows(rows);
    }

    @UiHandler("text")
    public void onTextChanged(ValueChangeEvent<String> value) {
        scheduleChangedPreview();

        ValueChangeEvent.fire(this, text.getValue());
    }

    @UiHandler("isDisplayPreview")
    public void onDisplayPreviewChanged( ClickEvent event ) {
        if (displayPreviewHandler != null) {
            displayPreviewHandler.onDisplayPreviewChanged( isDisplayPreview.getValue() );
            previewChanged();
        }
    }

    @UiHandler("text")
    public void onBase64Pasted(PasteEvent event) {
        if (event.getJsons() != null && !event.getJsons().isEmpty()) {
            fileUploader.uploadBase64Files(event.getJsons(), new PasteInfo(event.getStrPos()));
        } else {
            fileUploader.uploadBase64File(event.getJson(), new PasteInfo(event.getStrPos()));
        }
    }

    public void setDropZonePanel(HTMLPanel panel){
        text.setDropZonePanel(panel);
    }

    public void setEnsureDebugId(String debugId) {
        text.ensureDebugId(debugId);
    }

    private void scheduleChangedPreview() {
        changedPreviewTimer.cancel();
        changedPreviewTimer.schedule(PREVIEW_CHANGE_DELAY_MS);
    }

    private void previewChanged() {

        String value = text.getValue();

        previewContainer.setVisible( !isBlank(value) && renderer != null);

        if(!isDisplayPreview.getValue()) {
            preview.setInnerHTML("");
            return;
        }

        renderer.render(value, text -> {
            if (isBlank(text)) {
                previewContainer.setVisible(false);
                return;
            }
            previewContainer.setVisible(true);
            preview.setInnerHTML(text);
        });
    }

    @UiField
    Lang lang;
    @UiField
    DndAutoResizeTextArea text;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    DivElement preview;
    @UiField
    ToggleButton isDisplayPreview;

    private HTMLRenderer renderer;

    private final Timer changedPreviewTimer = new Timer() {
        @Override
        public void run() {
            previewChanged();
        }
    };

    private static final int PREVIEW_CHANGE_DELAY_MS = 200;
    private AbstractAttachmentUploader fileUploader;

    interface MarkdownAreaWithPreviewUiBinder extends UiBinder<HTMLPanel, MarkdownAreaWithPreview> {}
    private static MarkdownAreaWithPreviewUiBinder ourUiBinder = GWT.create( MarkdownAreaWithPreviewUiBinder.class );
}
