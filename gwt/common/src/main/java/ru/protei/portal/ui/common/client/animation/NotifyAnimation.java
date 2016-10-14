package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

import java.awt.*;

/**
 * Анимация уведомления
 */
public class NotifyAnimation {

    public void setWrapper( HasWidgets wrapper ) {
        this.parentWrapper = wrapper;
    }
    public void show( final IsWidget notify ) {
        //notify.asWidget().removeStyleName("notify-hide" );
        notify.asWidget().addStyleName("active" );

        parentWrapper.add(notify.asWidget());

        Timer prepareCloseTimer = new Timer() {
            @Override
            public void run() {
                close( notify );
            }
        };

        prepareCloseTimer.schedule(AUTO_CLOSE_TIME );
    }

    public void close( final IsWidget notify ) {

        notify.asWidget().removeStyleName("active");

        notify.asWidget().getElement().getStyle().setMarginBottom(
                notify.asWidget().getOffsetHeight() * -1, Style.Unit.PX
        );

        Timer closeTimer = new Timer() {
            @Override
            public void run() {
                notify.asWidget().removeFromParent();
            }
        };

        closeTimer.schedule(CLOSE_TIME);
    }

    private HasWidgets parentWrapper;
    private static final int AUTO_CLOSE_TIME = 5000;
    private static final int CLOSE_TIME = 300;
}
