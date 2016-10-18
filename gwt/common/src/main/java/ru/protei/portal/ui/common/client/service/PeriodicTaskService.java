package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.Timer;

import java.util.List;

/**
 * Сервис блочного заполнения списков
 */
public class PeriodicTaskService {
    public interface PeriodicTaskHandler {
        void cancel();
    }

    public interface TaskConsumer<T> {
        void consume( T value );
    }

    public <T> PeriodicTaskHandler startPeriodicTask(
            final List<T> items, final TaskConsumer<T> consumer, final int interval, final int blockSize ) {
        return new PeriodicTaskHandler() {

            @Override
            public void cancel() {
                task.cancel();
            }

            TimerTask<T> task = new TimerTask<T>( items, consumer, interval, blockSize );
        };
    };

    private class TimerTask<T> extends Timer {

        public TimerTask( List<T> items, TaskConsumer<T> consumer, int interval, int blockSize ) {
            this.items = items;
            this.consumer = consumer;
            this.blockSize = blockSize;

            scheduleRepeating( interval );
        }

        @Override
        public void run() {
            int count = 0;
            while ( !items.isEmpty() ) {
                consumer.consume( items.get( 0 ) );
                items.remove( 0 );
                if ( count++ > blockSize ) {
                    break;
                }
            }

            if ( items.isEmpty() ) {
                cancel();
            }
        }

        List<T> items;
        TaskConsumer<T> consumer;
        int blockSize;
    }
}
