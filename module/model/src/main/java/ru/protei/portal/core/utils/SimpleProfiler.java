package ru.protei.portal.core.utils;



/**
 * Простой профайлер
 * измеряет число милисекунд между вызовами
 * методов time() или check(String)
 *
 * SimpleProfiler profiler = new SimpleProfiler( SimpleProfiler.ON, new SimpleProfiler.Appender() {
 *      @Override
 *      public void append( String message, double currentTime ) {
 *              log.info( sb.toString() );
 *      } } );
 *
 *         profiler.start("Start test profiling);
 *         ...
 *         profiler.check("Time from start to current point =")
 *         ...
 *         profiler.check("Time from previous point =")
 *         ...
 *         profiler.push(); //  сохранить текущее значение таймера
 *
 *         double summOfTimes = 0D;
 *         int manyTimes = 100000;
 *         for(int i=0; i < manyTimes; ++i){
 *              //do some work
 *             summOfTimes += profiler.time();
 *         }
 *         log.info( "average="+summOfTimes/manyTimes );
 *
 *         profiler.pop(); //  вернуть предыдущее сохраненное значение таймера
 *          ...
 *
 *         profiler.stop("End test profiling);
 */
public class SimpleProfiler {
    private Appender appender;

    public interface Appender {
        void append( String message, long currentTime );
    }

    public SimpleProfiler( boolean isOn, Appender appender ) {
        this.isOn = isOn;
        this.appender = appender;
    }

    public SimpleProfiler( boolean isOn ) {
        this.isOn = isOn;
    }

    public void start( String message ) {
        if ( !isOn ) return;

        previousTime = System.currentTimeMillis();
        check( message );
    }

    public void stop( String message ) {
        if ( !isOn ) return;

        check( message );
        previousTime = 0;
        currentTime = 0;
    }

    public void pop() {
        if ( stakIndex < 0 ) stakIndex = 0;
        previousTime = stack[stakIndex--];
    }

    public void push() {
        if ( ++stakIndex > 10 ) stakIndex = 10;
        stack[stakIndex] = previousTime;
    }

    public double time() {
        if ( !isOn ) return 0;

        currentTime = System.currentTimeMillis() - previousTime;
        previousTime = System.currentTimeMillis();
        return currentTime;
    }

    public String check( String message ) {
        if ( !isOn ) return null;

        time();
        if ( null != appender ) {
            appender.append( message, currentTime );
            return null;
        } else {
            return message + " " + Double.toString( currentTime );
        }
    }

    public static final boolean ON = true;
    public static final boolean OFF = false;

    long stack[] = new long[10];
    int stakIndex = -1;
    long currentTime;
    long previousTime;
    boolean isOn;
}