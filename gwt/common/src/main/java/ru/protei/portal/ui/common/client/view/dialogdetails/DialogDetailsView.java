package ru.protei.portal.ui.common.client.view.dialogdetails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.animation.DialogAnimation;

/**
 * Вид для карточки
 */
public class DialogDetailsView extends PopupPanel implements AbstractDialogDetailsView {

    @Inject
    public DialogDetailsView( DialogAnimation animation ) {
        this.dialogAnimation = animation;
        add( ourUiBinder.createAndBindUi( this ) );
        setGlassEnabled( true );
        setAutoHideEnabled( false );
        ensureDebugIds();

        animation.setDialog( modalDialog, this );
    }

    @Override
    public void setActivity( AbstractDialogDetailsActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getBodyContainer() {
        return bodyContainer;
    }

    @Override
    public void showPopup() {
        getDialogAnimation().show();
        isSaveEnabled = true;
    }

    @Override
    public void hidePopup() {
        isSaveEnabled = false;
        getDialogAnimation().hide();
    }

    @Override
    public HasVisibility removeButtonVisibility() {
        return remove;
    }

    @Override
    public HasVisibility saveButtonVisibility() {
        return save;
    }

    @Override
    public HasEnabled removeButtonEnabled() {
        return remove;
    }

    @Override
    public HasEnabled saveButtonEnabled() {
        return save;
    }

    public DialogAnimation getDialogAnimation() {
        return dialogAnimation;
    }

    @Override
    public void setHeader(String value) {
        this.header.setInnerText(value);
    }

    @Override
    public void addStyleName(String value) {
        this.modalDialog.addClassName(value);
    }

    @Override
    public void setSaveOnEnterClick(boolean isSaveOnEnterClick) {
        this.isSaveOnEnterClick = isSaveOnEnterClick;
    }

    @Override
    public void setSaveButtonName( String name ) {
        save.setText( name );
    }

    @Override
    public void setCancelVisible( boolean isCancelVisible ) {
        cancel.setVisible( isCancelVisible );
    }

    @Override
    public void setCloseVisible( boolean isCloseVisible ) {
        close.setVisible( isCloseVisible );
    }

    @UiHandler( "save" )
    public void onSaveClicked( ClickEvent event ) {
        event.preventDefault();
        fireSaveClicked();
    }

    @UiHandler( {"cancel","close"} )
    public void onCancelClicked( ClickEvent event ) {
        event.preventDefault();
        fireCancelClicked();
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        event.preventDefault();
        fireRemoveClicked();
    }

    @Override
    protected void onPreviewNativeEvent( Event.NativePreviewEvent event ) {
        super.onPreviewNativeEvent( event );

        if ( event.getTypeInt() == Event.ONKEYDOWN ) {
            boolean isEscapeClicked = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE;
            boolean isEnterClicked = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
            if (isEscapeClicked) {
                fireCancelClicked();
            } else if (isEnterClicked && isSaveOnEnterClick) {
                fireSaveClicked();
            }
        }
    }

    private void ensureDebugIds() {
        modalDialog.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DIALOG_DETAILS.MODAL_DIALOG);
        header.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DIALOG_DETAILS.NAME);
        save.ensureDebugId(DebugIds.DIALOG_DETAILS.SAVE_BUTTON);
        cancel.ensureDebugId(DebugIds.DIALOG_DETAILS.CANCEL_BUTTON);
        remove.ensureDebugId(DebugIds.DIALOG_DETAILS.REMOVE_BUTTON);
        close.ensureDebugId(DebugIds.DIALOG_DETAILS.CLOSE_BUTTON);
    }

    private void fireCancelClicked() {
        if ( activity != null ) {
            activity.onCancelClicked();
        }
    }

    private void fireSaveClicked() {
        if ( isSaveEnabled && activity != null ) {
            activity.onSaveClicked();
        }
    }

    private void fireRemoveClicked() {
        if ( activity != null ) {
            activity.onRemoveClicked();
        }
    }

    @UiField
    HTMLPanel bodyContainer;
    @UiField
    Anchor save;
    @UiField
    Anchor cancel;
    @UiField
    Anchor remove;
    @UiField
    HeadingElement header;
    @UiField
    Button close;
    @UiField
    DivElement modalDialog;

    AbstractDialogDetailsActivity activity;

    private DialogAnimation dialogAnimation;
    private boolean isSaveEnabled;
    private boolean isSaveOnEnterClick = true;

    interface DetailsViewUiBinder extends UiBinder<HTMLPanel, DialogDetailsView> {}
    private static DetailsViewUiBinder ourUiBinder = GWT.create( DetailsViewUiBinder.class );
}