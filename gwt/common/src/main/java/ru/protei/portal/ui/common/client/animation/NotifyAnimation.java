package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Анимация нотификаций
 */
public class NotifyAnimation {

    public void setWrapper( HasWidgets wrapper ) {
        this.wrapper = wrapper;
    }
    public void show( final IsWidget notify ) {
        notify.asWidget().removeStyleName( "active" );
        notify.asWidget().addStyleName( "active" );

        wrapper.add( notify.asWidget() );

        Timer prepareCloseTimer = new Timer() {
            @Override
            public void run() {
                close( notify );
            }
        };

        prepareCloseTimer.schedule( AUTO_CLOSE_TIME );
    }

    public void close( final IsWidget notify ) {
        notify.asWidget().removeStyleName( "active" );
        notify.asWidget().addStyleName( "active" );

        Timer closeTimer = new Timer() {
            @Override
            public void run() {
                notify.asWidget().removeFromParent();
            }
        };

        closeTimer.schedule( CLOSE_TIME );
    }

    private HasWidgets wrapper;
    private static final int AUTO_CLOSE_TIME = 5000;
    private static final int CLOSE_TIME = 300;
}
