package ru.protei.portal.ui.common.client.throttler;

import com.google.gwt.user.client.Timer;

/**
 * Действие будет запущено однократно по истечению времени задержки
 * вызовы до истечения задержки игнорируются
 */
public class DelayedThrottler extends BaseThrottler {

    /**
     * Действие будет запущено однократно по истечению времени задержки
     * вызовы до истечения задержки игнорируются
     *
     * @param delay  задержка в миллисекундах
     * @param action действие которое нужно выполнить
     */
    public DelayedThrottler(int delay, Action action) {
        super(delay, action);
    }

    @Override
    public void run() {

        if (timer.isRunning()) {
            return;
        }

        timer.schedule(milliseconds);
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            doAction();
        }
    };
}


