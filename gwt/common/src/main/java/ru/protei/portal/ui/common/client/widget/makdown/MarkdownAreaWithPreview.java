package ru.protei.portal.ui.common.client.widget.makdown;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
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
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.imagepastetextarea.ImagePasteTextArea;
import ru.protei.portal.ui.common.shared.model.HTMLRenderer;

import java.util.function.BiConsumer;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public class MarkdownAreaWithPreview
        extends Composite
        implements HasValue<String> {

    public MarkdownAreaWithPreview() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        addPasteHandler(text.getElement(), this);
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

    public void setPasteHandler(BiConsumer<String, Integer> pasteHandler) {
        this.pasteHandler = pasteHandler;
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

    public void onPastedObject(JavaScriptObject object, Integer strPos) {
        if (pasteHandler != null) {
            ImagePasteTextArea.Base64Image base64Image = object.cast();
            String json = JsonUtils.stringify(base64Image);
            pasteHandler.accept(json, strPos);
        }
    }

    private native void addPasteHandler(Element element, MarkdownAreaWithPreview view) /*-{
        element.addEventListener("paste", function(event) {
            var matchType = new RegExp("image.*");
            var clipboardData = event.clipboardData;

            var strPos = 0;
            var br = ((element.selectionStart || element.selectionStart == '0') ?
                "ff" : (document.selection ? "ie" : false));
            if (br == "ie") {
                element.focus();
                var range = document.selection.createRange();
                range.moveStart('character', -element.value.length);
                strPos = range.text.length;
            } else if (br == "ff") {
                strPos = element.selectionStart;
            }

            var found = false;
            return Array.prototype.forEach.call(clipboardData.types, function (type, i) {
                var file, reader;
                if (found) {
                    return;
                }
                if (type.match(matchType) || clipboardData.items[i].type.match(matchType)) {
                    file = clipboardData.items[i].getAsFile();
                    reader = new FileReader();
                    reader.onload = function (evt) {
                        var data = {
                            base64: evt.target.result,
                            name: file.name,
                            type: file.type,
                            size: file.size
                        };
                        view.@ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview::onPastedObject(*)(data, @java.lang.Integer::valueOf(I)(strPos));
                    };
                    reader.readAsDataURL(file);
                    return (found = true);
                };
            });
        });
    }-*/;

    @UiField
    AutoResizeTextArea text;
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
    private BiConsumer<String, Integer> pasteHandler;

    interface MarkdownAreaWithPreviewUiBinder extends UiBinder<HTMLPanel, MarkdownAreaWithPreview> {}
    private static MarkdownAreaWithPreviewUiBinder ourUiBinder = GWT.create( MarkdownAreaWithPreviewUiBinder.class );
}