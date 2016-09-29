package ru.protei.portal.ui.common.shared.exception;


/**
 * Исключение, выбрасываемое при неудачных попытках обратиться на сервер
 */
public class RequestFailedException extends Exception {

    public RequestFailedException( String msg ) {
        super( msg );
    }

    public RequestFailedException() {}
}
