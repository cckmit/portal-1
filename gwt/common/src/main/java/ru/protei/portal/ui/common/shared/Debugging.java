package ru.protei.portal.ui.common.shared;

/**
 * Created by bondarenko on 26.10.16.
 */
public class Debugging {

    native public static void consoleLog( String message) /*-{
        console.log( "console: " + message );
    }-*/;

    native public static void debugger() /*-{
        debugger;
    }-*/;

}
