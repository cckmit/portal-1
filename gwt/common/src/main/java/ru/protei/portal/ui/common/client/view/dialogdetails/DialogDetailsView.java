package ru.protei.portal.ui.common.client.view.dialogdetails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
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
import ru.protei.portal.ui.common.client.model.marker.HasProcessable;
import ru.protei.portal.ui.common.client.widget.button.ButtonProcessable;

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
    public HasVisibility cancelButtonVisibility() {
        return cancel;
    }

    @Override
    public HasProcessable saveButtonProcessable() {
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

    @Override
    public HasEnabled cancelButtonEnabled() {
        return cancel;
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

    @Override
    public void setAdditionalVisible(boolean isAdditionalVisible) {
        additional.setVisible(isAdditionalVisible);
    }

    @Override
    public void setAdditionalButtonName(String name) {
        additional.setText(name);
    }

    @Override
    public void addBodyStyleName(String styleName) {
        bodyContainer.addStyleName(styleName);
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

    @UiHandler( "additional" )
    public void onAdditionalClicked( ClickEvent event ) {
        event.preventDefault();
        fireAdditionalClicked();
    }

    @Override
    protected void onPreviewNativeEvent( Event.NativePreviewEvent event ) {
        super.onPreviewNativeEvent( event );

        if ( event.getTypeInt() == Event.ONKEYDOWN ) {
            boolean isEscapeClicked = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE;
            boolean isEnterClicked = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
            if (isEscapeClicked && !isAnySelectorPopupOpened(bodyContainer.getElement())) {
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
        additional.ensureDebugId(DebugIds.DIALOG_DETAILS.ADDITIONAL_BUTTON);
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

    private void fireAdditionalClicked() {
        if ( activity != null ) {
            activity.onAdditionalClicked();
        }
    }

    private native boolean isAnySelectorPopupOpened(Element bodyContainerElement) /*-{
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
    HTMLPanel bodyContainer;
    @UiField
    ButtonProcessable save;
    @UiField
    Button cancel;
    @UiField
    Button remove;
    @UiField
    Button additional;
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
