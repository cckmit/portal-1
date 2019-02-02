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
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.util.MarkdownClient;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;

public class MarkdownAreaWithPreview
        extends Composite
        implements HasValue<String> {

    public MarkdownAreaWithPreview() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvent) {
        text.setValue(value);
        if ( fireEvent ) {
            ValueChangeEvent.fire(this, text.getValue());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
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

    private void fireChangedPreview() {
        String value = text.getValue();
        if (StringUtils.isNotBlank(value)) {
            value = markdownClient.plain2escaped2markdown(value);
        }
        if (StringUtils.isBlank(value)) {
            previewContainer.setVisible(false);
        } else {
            previewContainer.setVisible(true);
            preview.setInnerHTML(value);
        }
    }

    @UiField
    AutoResizeTextArea text;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    DivElement preview;

    private MarkdownClient markdownClient = new MarkdownClient();

    private final Timer changedPreviewTimer = new Timer() {
        @Override
        public void run() {
            fireChangedPreview();
        }
    };

    private static final int PREVIEW_CHANGE_DELAY_MS = 200;

    interface MarkdownAreaWithPreviewUiBinder extends UiBinder<HTMLPanel, MarkdownAreaWithPreview> {}
    private static MarkdownAreaWithPreviewUiBinder ourUiBinder = GWT.create( MarkdownAreaWithPreviewUiBinder.class );


}