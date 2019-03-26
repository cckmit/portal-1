package ru.protei.portal.core.exception;

import ru.protei.portal.core.model.dict.En_ResultStatus;

public class ResultStatusException extends RuntimeException {

    public ResultStatusException(En_ResultStatus resultStatus) {
        super();
        this.resultStatus = resultStatus;
    }

    public ResultStatusException(En_ResultStatus resultStatus, Throwable cause) {
        super(cause);
        this.resultStatus = resultStatus;
    }

    private final En_ResultStatus resultStatus;

    public En_ResultStatus getResultStatus() {
        return resultStatus;
    }
}
