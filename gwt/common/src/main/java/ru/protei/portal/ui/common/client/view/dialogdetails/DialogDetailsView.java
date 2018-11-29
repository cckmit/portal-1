package ru.protei.portal.ui.common.client.view.dialogdetails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
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
public class DialogDetailsView extends PopupPanel implements AbstractDialogDetailsView, DialogAnimation.AnimationHandler {

    @Inject
    public DialogDetailsView( DialogAnimation animation ) {
        this.dialogAnimation = animation;
        dialogAnimation.setCompleteHandler(this);
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
        getDialogAnimation().hide();
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
        fireSaveClicked();
    }

    @UiHandler( {"cancel","close"} )
    public void onCancelClicked( ClickEvent event ) {
        event.preventDefault();
        fireCancelClicked();
    }

    @Override
    public void onAnimationComplete() {
        isSaveEnabled = true;
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
            isSaveEnabled = false;
            activity.onSaveClicked();
        }
    }

    @UiField
    HTMLPanel bodyContainer;
    @UiField
    Anchor save;
    @UiField
    Anchor cancel;
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