package ru.protei.portal.ui.common.client.throttler;

import com.google.gwt.user.client.Timer;

/**
 * Действие будет запущено при вызове метода run немедленно и по истечению таймаута
 * если в течение таймаута был один или более вызовов
 */
public class ImmediateAndAfterDelayIfRunningDuringDelayThrottler extends BaseThrottler {
    public ImmediateAndAfterDelayIfRunningDuringDelayThrottler(int milliseconds, Action action) {
        super(milliseconds, action);
    }

    @Override
    public void run() {
        if (!alreadyExecuteImmediate) {
            alreadyExecuteImmediate = true;
            action.run();
        }

        if (timer.isRunning()) {
            runDuringDelay = true;
            return;
        }

        timer.schedule(milliseconds);
    }


    Timer timer = new Timer() {

        @Override
        public void run() {
            if (runDuringDelay) {
                doAction();
            }
            alreadyExecuteImmediate = false;
        }
    };

    private boolean alreadyExecuteImmediate;
    private boolean runDuringDelay;
}


