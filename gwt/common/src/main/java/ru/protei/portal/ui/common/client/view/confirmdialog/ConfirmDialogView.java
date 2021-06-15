package ru.protei.portal.ui.common.client.view.confirmdialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.confirmdialog.AbstractConfirmDialogActivity;
import ru.protei.portal.ui.common.client.activity.confirmdialog.AbstractConfirmDialogView;

/**
 * Представление окна подтверждения.
 */
public class ConfirmDialogView extends PopupPanel implements AbstractConfirmDialogView {

    public ConfirmDialogView() {
        setWidget( ourUiBinder.createAndBindUi( this ) );
        setGlassEnabled( true );
        setGlassStyleName( "confirm-overlay" );
        setAutoHideEnabled( false );
        ensureDebugIds();

        resizeHandler = resizeEvent -> getElement().getStyle().setLeft((Window.getClientWidth() / 3.0), Style.Unit.PX);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        confirmButton.ensureDebugId(DebugIds.CONFIRM_DIALOG.OK_BUTTON);
        cancelButton.ensureDebugId(DebugIds.CONFIRM_DIALOG.CANCEL_BUTTON);
    }

    @Override
    public void setActivity( AbstractConfirmDialogActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setText( String text ) {
        description.setInnerText( text );
    }

    @Override
    public HasText confirmButtonText() {
        return confirmButton;
    }

    @Override
    public HasText cancelButtonText() {
        return cancelButton;
    }

    @UiHandler("confirmButton")
    public void onConfirmClick( ClickEvent event ) {
        activity.onConfirmClicked();
    }

    @UiHandler("cancelButton")
    public void onCancelClick( ClickEvent event ) {
        activity.onCancelClicked();
    }

    @Override
    protected void onLoad() {
        resizeHandlerReg = Window.addResizeHandler( resizeHandler );
    }

    @Override
    protected void onUnload() {
        resizeHandlerReg.removeHandler();
    }

    @UiField
    Button confirmButton;

    @UiField
    ParagraphElement description;

    @UiField
    Button cancelButton;

    private ResizeHandler resizeHandler;
    private HandlerRegistration resizeHandlerReg;
    private AbstractConfirmDialogActivity activity;

    interface ConfirmDialogViewUiBinder extends UiBinder<HTMLPanel, ConfirmDialogView> {}

    private static ConfirmDialogViewUiBinder ourUiBinder = GWT.create( ConfirmDialogViewUiBinder.class );

}