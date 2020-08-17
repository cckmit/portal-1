package ru.protei.portal.ui.common.client.throttler;

import com.google.gwt.user.client.Timer;

/**
 * Действие будет запущено однократно по истечению времени задержки от последнего запуска
 */
public class DelayedAntiRapidThrottler extends BaseThrottler {

    /**
     * Действие будет запущено однократно по истечению времени задержки от последнего запуска
     *
     * @param delay  задержка в миллисекундах от последнего запуска
     * @param action действие которое нужно выполнить
     */
    public DelayedAntiRapidThrottler( int delay, Action action) {
        super(delay, action);
    }

    @Override
    public void run() {
        timer.cancel();
        timer.schedule(milliseconds);
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            doAction();
        }
    };
}


