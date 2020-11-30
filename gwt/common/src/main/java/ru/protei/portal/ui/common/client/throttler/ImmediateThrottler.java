package ru.protei.portal.ui.common.client.throttler;

import com.google.gwt.user.client.Timer;

/**
 * Действие будет запущено однократно при вызове метода run,
 * прочие вызовы run игнорируются до истечения времени задержки
 */
public class ImmediateThrottler extends BaseThrottler {
    public ImmediateThrottler(int ignoringTimeout, Action action) {
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
            alreadyExecuteImmediate = false;
        }
    };


    private boolean alreadyExecuteImmediate;
}


