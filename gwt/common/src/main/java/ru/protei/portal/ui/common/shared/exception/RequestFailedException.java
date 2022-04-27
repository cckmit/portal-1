package ru.protei.portal.ui.common.shared.exception;


import ru.protei.portal.core.model.dict.En_IssueValidationResult;
import ru.protei.portal.core.model.dict.En_ResultStatus;

/**
 * Исключение, выбрасываемое при неудачных попытках обратиться на сервер
 */
public class RequestFailedException extends Exception {

    public RequestFailedException( En_ResultStatus status ) {
        super( );
        this.status = status;
    }

    public RequestFailedException(En_IssueValidationResult issueValidationResult) {
        this(En_ResultStatus.VALIDATION_ERROR);
        this.issueValidationResult = issueValidationResult;
    }

    public En_ResultStatus status;
    public En_IssueValidationResult issueValidationResult;

    public RequestFailedException() {}
}
