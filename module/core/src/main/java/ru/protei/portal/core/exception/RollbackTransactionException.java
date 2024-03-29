package ru.protei.portal.core.exception;

import ru.protei.portal.core.model.dict.En_ResultStatus;

public class RollbackTransactionException extends RuntimeException {

    public RollbackTransactionException(En_ResultStatus resultStatus) {
        super(resultStatus.name());
        this.resultStatus = resultStatus;
    }

    public RollbackTransactionException(En_ResultStatus resultStatus, String message) {
        super(resultStatus.name() + " - " + message);
        this.resultStatus = resultStatus;
    }

    public RollbackTransactionException(En_ResultStatus resultStatus, Throwable cause) {
        super(resultStatus.name(), cause);
        this.resultStatus = resultStatus;
    }

    private final En_ResultStatus resultStatus;

    public En_ResultStatus getResultStatus() {
        return resultStatus;
    }
}
