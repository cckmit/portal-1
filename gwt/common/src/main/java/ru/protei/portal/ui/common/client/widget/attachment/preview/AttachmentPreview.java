package ru.protei.portal.ui.common.client.widget.attachment.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;

import static com.google.gwt.event.dom.client.KeyCodes.KEY_ESCAPE;

/**
 * Created by bondarenko on 29.06.17.
 */
public class AttachmentPreview extends PopupPanel implements ClickHandler{

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));

        root.sinkEvents(Event.ONCLICK);
        root.addHandler(this, ClickEvent.getType());
        addStyleName("max-z-index");
    }

    @Override
    protected void onPreviewNativeEvent( Event.NativePreviewEvent event ) {
        super.onPreviewNativeEvent( event );

        if ( event.getTypeInt() == Event.ONKEYDOWN ) {
            boolean isEscapeClicked = event.getNativeEvent().getKeyCode() == KEY_ESCAPE;
            if (isEscapeClicked && !isAnyPopupOpened(root.getElement())) {
                hide();
            }
        }
    }

    public void show(Image attachment){
        attachment.ensureDebugId(DebugIds.ATTACHMENT.IMAGE);
        root.clear();
        root.add(attachment);
        show();
    }

    public void onClick(ClickEvent event){
        hide();
    }

    private native boolean isAnyPopupOpened(Element bodyContainerElement) /*-{
        var selectors = bodyContainerElement.querySelectorAll('.selector-popup');

        if (selectors.length === 0) {
            return false;
        }

        for (var i = 0; i < selectors.length; i++) {
            if (!selectors[i].style.display || selectors[i].style.display !== "none") {
                return true;
            }
        }

        return false;
    }-*/;

    @UiField
    HTMLPanel root;

    private static AttachmentPreviewUiBinder ourUiBinder = GWT.create(AttachmentPreviewUiBinder.class);
    interface AttachmentPreviewUiBinder extends UiBinder<HTMLPanel, AttachmentPreview> {}
}
