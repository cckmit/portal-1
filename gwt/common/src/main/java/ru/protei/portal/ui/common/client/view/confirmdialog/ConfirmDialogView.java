package ru.protei.portal.ui.common.client.view.confirmdialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
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

        resizeHandler = new ResizeHandler() {
            @Override
            public void onResize( ResizeEvent resizeEvent ) {
                getElement().getStyle().setLeft( ( Window.getClientWidth() / 3 ), Style.Unit.PX );
            }
        };
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
    public void setConfirmButtonText( String text ) {
        confirmButton.setText( text );
    }

    @UiHandler("confirmButton")
    public void onConfirmClick( ClickEvent event ) {
        if ( activity != null ) {
            activity.onConfirmClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClick( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCancelClicked();
        }
    }

    @Override
    protected void onLoad() {
        resizeHandlerReg = Window.addResizeHandler( resizeHandler );
    }

    @Override
    protected void onUnload() {
        resizeHandlerReg.removeHandler();
    }

    ResizeHandler resizeHandler;

    HandlerRegistration resizeHandlerReg;

    @UiField
    Button confirmButton;

    @UiField
    ParagraphElement description;

    @UiField
    Button cancelButton;

    AbstractConfirmDialogActivity activity;

    interface ConfirmDialogViewUiBinder extends UiBinder<HTMLPanel, ConfirmDialogView> {}

    private static ConfirmDialogViewUiBinder ourUiBinder = GWT.create( ConfirmDialogViewUiBinder.class );

}