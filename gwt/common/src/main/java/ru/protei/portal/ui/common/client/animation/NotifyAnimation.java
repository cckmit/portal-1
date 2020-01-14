package ru.protei.portal.ui.common.client.animation;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.HashMap;
import java.util.Map;

/**
 * Анимация уведомления
 */
public class NotifyAnimation {

    public void setWrapper( HasWidgets wrapper ) {
        this.parentWrapper = wrapper;
    }
    public void show( final IsWidget notify ) {
        notify.asWidget().addStyleName("active" );

        currentOpacityMap.put(notify, 1.0);

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

        Timer opacityTimer = new Timer() {
            @Override
            public void run() {
                currentOpacityMap.put(notify, currentOpacityMap.get(notify) - 1000.0 / (FRAMES_PER_SECOND * CLOSE_TIME) );
                notify.asWidget().getElement().getStyle().setOpacity(currentOpacityMap.get(notify));
            }
        };

        Timer closeTimer = new Timer() {
            @Override
            public void run() {
                notify.asWidget().removeFromParent();
                opacityTimer.cancel();
                currentOpacityMap.remove(notify);
            }
        };

        opacityTimer.scheduleRepeating(1000 / FRAMES_PER_SECOND);
        closeTimer.schedule(CLOSE_TIME);
    }

    private static final int FRAMES_PER_SECOND = 25;
    private Map<IsWidget, Double> currentOpacityMap = new HashMap<>();
    private HasWidgets parentWrapper;
    private static final int AUTO_CLOSE_TIME = 5000;
    private static final int CLOSE_TIME = 500;
}
