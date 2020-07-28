package ru.protei.portal.ui.common.client.throttler;

public abstract class BaseThrottler implements Throttler {
    public BaseThrottler(int milliseconds, Action action) {
        this.milliseconds = milliseconds;
        this.action = action;
    }

    protected void doAction() {
        if (action != null) {
            action.run();
        }
    }

    @Override
    public void setAction(Action action) {
        this.action = action;
    }

    protected Action action;
    protected int milliseconds;
}
