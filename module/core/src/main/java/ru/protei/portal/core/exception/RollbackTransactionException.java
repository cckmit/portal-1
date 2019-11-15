package ru.protei.portal.core.exception;

public class RollbackTransactionException extends RuntimeException {

    public RollbackTransactionException( String message ) {
        super( message );
    }
}
