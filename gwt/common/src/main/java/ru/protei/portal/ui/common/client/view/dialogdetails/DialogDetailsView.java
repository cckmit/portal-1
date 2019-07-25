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

    public DialogAnimation getDialogAnimation() {
        return dialogAnimation;
    }

    @Override
    public void setHeader(String value) {
        this.header.setInnerText(value);
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
            if ( event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE ) {
                fireCancelClicked();
            } else if ( event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER ) {
                fireSaveClicked();
            }
        }
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

    interface DetailsViewUiBinder extends UiBinder<HTMLPanel, DialogDetailsView> {}
    private static DetailsViewUiBinder ourUiBinder = GWT.create( DetailsViewUiBinder.class );
}