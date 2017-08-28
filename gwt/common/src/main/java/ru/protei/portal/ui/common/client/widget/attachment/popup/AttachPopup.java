package ru.protei.portal.ui.common.client.widget.attachment.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;

import java.util.List;

/**
 * Created by bondarenko on 27.12.16.
 */
public class AttachPopup extends PopupPanel{

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled( true );
        setAutoHideOnHistoryEventsEnabled( true );

        resizeHandler = event -> {
            if ( isAttached() ) {
                showNear( relative );
            }
        };

        windowScrollHandler = event -> {
            if ( isAttached() ) {
                showNear( relative );
            }
        };
    }

    public void showNear( IsWidget nearWidget ) {
        this.relative = nearWidget;

        showRelativeTo( nearWidget.asWidget() );
        root.getElement().getStyle().setPosition( Style.Position.RELATIVE );
        root.getElement().getStyle().setDisplay( Style.Display.BLOCK );
    }

    public void fill(List<Attachment> attachments){
        if(attachments == null)
            return;

        attachmentContainer.clear();
        attachmentContainer.add(attachments);
    }

    @Override
    protected void onLoad() {
        resizeHandlerReg = Window.addResizeHandler( resizeHandler );
        scrollHandlerReg = Window.addWindowScrollHandler(windowScrollHandler);
    }

    @Override
    protected void onUnload() {
        if ( resizeHandlerReg != null ) {
            resizeHandlerReg.removeHandler();
            scrollHandlerReg.removeHandler();
        }
    }


    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentContainer;

    IsWidget relative;
    ResizeHandler resizeHandler;
    Window.ScrollHandler windowScrollHandler;
    HandlerRegistration resizeHandlerReg;
    HandlerRegistration scrollHandlerReg;

    private static AttachPopupUiBinder ourUiBinder = GWT.create(AttachPopupUiBinder.class);
    interface AttachPopupUiBinder extends UiBinder<HTMLPanel, AttachPopup> {}
}