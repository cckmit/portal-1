package ru.protei.portal.ui.common.client.widget.makdown;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.shared.model.HTMLRenderer;

public class MarkdownAreaWithPreview
        extends Composite
        implements HasValue<String>, HasText {

    public MarkdownAreaWithPreview() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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
    public String getText() {
        return getValue();
    }

    @Override
    public void setText(String text) {
        setValue(text);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    public void setRenderer(HTMLRenderer renderer) {
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

    private void scheduleChangedPreview() {
        changedPreviewTimer.cancel();
        changedPreviewTimer.schedule(PREVIEW_CHANGE_DELAY_MS);
    }

    private void previewChanged() {

        String value = text.getValue();

        if (StringUtils.isBlank(value) || renderer == null) {
            previewContainer.setVisible(false);
        }

        renderer.render(value, text -> {
            if (StringUtils.isBlank(text)) {
                previewContainer.setVisible(false);
                return;
            }
            previewContainer.setVisible(true);
            preview.setInnerHTML(text);
        });
    }

    @UiField
    AutoResizeTextArea text;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    DivElement preview;

    private HTMLRenderer renderer;

    private final Timer changedPreviewTimer = new Timer() {
        @Override
        public void run() {
            previewChanged();
        }
    };

    private static final int PREVIEW_CHANGE_DELAY_MS = 200;

    interface MarkdownAreaWithPreviewUiBinder extends UiBinder<HTMLPanel, MarkdownAreaWithPreview> {}
    private static MarkdownAreaWithPreviewUiBinder ourUiBinder = GWT.create( MarkdownAreaWithPreviewUiBinder.class );


}