package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;
import ru.protei.portal.ui.common.client.view.dialogdetails.DialogDetailsView;

/**
 * Анимация диалогового окна
 */
public class DialogAnimation {

    public void show() {
        popup.setGlassStyleName( "dialog-overlay dialog-open" );
        dialog.removeClassName( "dialog-close" );
        dialog.addClassName( "dialog-open" );
        popup.center();
    }

    public void hide() {
        popup.setGlassStyleName( "dialog-overlay dialog-close" );
        dialog.removeClassName( "dialog-open" );
        dialog.addClassName( "dialog-close" );
        closeTimer.cancel();
        closeTimer.schedule( 1000 );
    }

    public void setDialog( Element dialog, PopupPanel popup ) {
        this.dialog = dialog;
        this.popup = popup;
    }

    private Timer closeTimer = new Timer() {
        @Override
        public void run() {
            popup.hide();
            closeTimer.cancel();
        }
    };

    private Element dialog;
    private PopupPanel popup;
}
