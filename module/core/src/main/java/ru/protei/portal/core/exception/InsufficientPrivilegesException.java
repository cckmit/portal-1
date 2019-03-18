package ru.protei.portal.core.exception;

/**
 * Created by admin on 10/07/2017.
 */
public class InsufficientPrivilegesException extends RuntimeException {
    public InsufficientPrivilegesException() {}
    public InsufficientPrivilegesException(String message) {
        super(message);
    }
}
