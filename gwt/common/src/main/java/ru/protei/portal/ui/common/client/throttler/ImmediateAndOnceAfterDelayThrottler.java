package ru.protei.portal.ui.common.client.throttler;

import com.google.gwt.user.client.Timer;

/**
 * Действие будет запущено при вызове метода run немедленно и однократно по истечению таймаута
 * Прочие вызовы run игнорируются до истечения времени задержки
 */
public class ImmediateAndOnceAfterDelayThrottler extends BaseThrottler {
    public ImmediateAndOnceAfterDelayThrottler(int ignoringTimeout, Action action) {
        super(ignoringTimeout, action);
    }

    @Override
    public void run() {
        if (!alreadyExecuteImmediate) {
            alreadyExecuteImmediate = true;
            action.run();
        }

        if (timer.isRunning()) {
            return;
        }

        timer.schedule(milliseconds);
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            doAction();
            alreadyExecuteImmediate = false;
        }
    };

    private boolean alreadyExecuteImmediate;
}


