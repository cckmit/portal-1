package ru.protei.portal.ui.common.client.throttler;

import ru.brainworm.factory.generator.activity.client.activity.Activity;

/**
 * Фабрика ограничителей количества выполняемых действий
 */
public class ThrottlerFactory {
    private ThrottlerFactory() {
    }

    /**
     * Ограничитель работающий по истечению задержки
     *
     * @param delay - задержка в милисекундах
     */
    public static DelayedBuilder delayed(int delay) {
        if (delay < 0) {
            return null;
        }
        return new DelayedBuilder(delay);
    }

    /**
     * Ограничитель выполняющий действие сразу и блокирующий последующие
     * вызовы до истечения времени
     *
     * @param ignoringTimeout - время игнорирования вызовов в милисекундах
     */
    public static ImmediateBuilder immediate(int ignoringTimeout) {
        if (ignoringTimeout < 0) {
            return null;
        }
        return new ImmediateBuilder(ignoringTimeout);
    }

    /**
     * Действие будет запущено однократно по истечению времени задержки
     *
     * @param delay  задержка в миллисекундах
     * @param action действие которое нужно выполнить
     */
    public static Throttler makeDelayedThrottler( int delay, Action action) {
        return new DelayedThrottler(delay, action);
    }


    /**
     * Действие будет запущено однократно по истечению времени задержки от последнего запуска
     *
     * @param delay  задержка в миллисекундах от последнего запуска
     * @param action действие которое нужно выполнить
     */
    public static Throttler makeDelayedAntiRapidThrottler( int delay, Action action) {
        return new DelayedAntiRapidThrottler(delay, action);
    }

    /**
     * Действие будет запущено однократно при вызове метода run,
     * прочие вызовы run игнорируются до истечения времени задержки
     *
     * @param ignoringTimeout игнорировать вызывы на протяжении времени в миллисекундах
     * @param action          действие которое нужно выполнить
     */
    public static Throttler makeImmediateThrottler( int ignoringTimeout, Action action) {
        return new ImmediateThrottler(ignoringTimeout, action);
    }

    /**
     * Действие будет запущено при вызове метода run немедленно и однократно по истечению таймаута
     * Прочие вызовы run игнорируются до истечения времени задержки
     *
     * @param ignoringTimeout игнорировать вызывы на протяжении времени в миллисекундах
     * @param action          действие которое нужно выполнить
     */
    public static Throttler makeImmediateAndOnceAfterDelayThrottler( int ignoringTimeout, Action action) {
        return new ImmediateAndOnceAfterDelayThrottler(ignoringTimeout, action);
    }

    /**
     * Действие будет запущено при вызове метода run немедленно и по истечению таймаута
     * если в течение таймаута был один или более вызовов
     */
    public static Throttler makeImmediateAndAfterDelayIfRunningDuringDelayThrottler( int ignoringTimeout, Action action) {
        return new ImmediateAndAfterDelayIfRunningDuringDelayThrottler(ignoringTimeout, action);
    }

    public static class DelayedBuilder {
        private final int delay;
        private Throttler throttler;
        private Action action;
        
        private DelayedBuilder(int delay) {
            this.delay = delay;
        }

        public DelayedBuilder action( Action action ){
            this.action = action;
            return this;
        }

        public Throttler build() {
            if (throttler != null) {
                throttler.setAction(action);
                return throttler;
            }
            return new DelayedThrottler(delay, action);
        }

        /**
         * Запусить однократно после истечения времени игнорирования
         */
        public DelayedBuilder runOnceAfterDelay() {
            throttler = new DelayedThrottler(delay, null);
            return this;
        }

        /**
         * Действие будет запущено однократно по истечению времени задержки от последнего запуска
         */
        public DelayedBuilder runOnceAfterLastCall( ) {
            throttler = new DelayedAntiRapidThrottler(delay, null);
            return this;
        }
    }

    public static class ImmediateBuilder {
        private ImmediateBuilder(int ignoringTimeout) {
            this.ignoringTimeout = ignoringTimeout;
        }

        private int ignoringTimeout;
        private Throttler throttler;
        private Action action;
        
        public ImmediateBuilder action( Action action ){
            this.action = action;
            return this;
        }

        public Throttler build() {
            if (throttler != null) {
                throttler.setAction(action);
                return throttler;
            }
            return new ImmediateThrottler(ignoringTimeout, action);
        }

        /**
         * Запусить еще раз после истечения времени игнорирования
         */
        ImmediateBuilder runOnceAfterDelay() {
            throttler = new ImmediateAndOnceAfterDelayThrottler(ignoringTimeout, null);
            return this;
        }

        /**
         * Запустить по истечению времени игнорирования
         * если были попытки вызова во время времени игнорирования
         */
        ImmediateBuilder runAfterDelayIfRunningDuringDelay() {
            throttler = new ImmediateAndAfterDelayIfRunningDuringDelayThrottler(ignoringTimeout, null);
            return this;
        }
    }
}
