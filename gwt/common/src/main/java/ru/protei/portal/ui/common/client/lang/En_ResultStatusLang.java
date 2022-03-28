package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ResultStatus;


/**
 * Описание статусов результатов операций
 */
public class En_ResultStatusLang {

    @Inject
    public En_ResultStatusLang(Lang lang) {
        this.lang = lang;
    }

    public String getMessage( En_ResultStatus value )
    {
        if (value == null)
            return lang.errUnknownResult();

        /**
         *  @TODO
         *  - после реализации обработки ответов от БД заменить грубые определения на конкретные статусы
         *   (должны уйти NOT_CREATED, NOT_UPDATED)
         */

        switch (value)
        {
            case OK : return lang.msgOK();
            case CONNECTION_ERROR: return lang.errConnectionError();
            case INTERNAL_ERROR : return lang.errInternalError();
            case INVALID_SESSION_ID :  return lang.errInvalidSessionID();
            case INVALID_LOGIN_OR_PWD :  return lang.errLoginOrPwd();
            case ACCOUNT_IS_LOCKED:  return lang.errAccountIsLocked();
            case GET_DATA_ERROR: return lang.errGetDataError();
            case NOT_FOUND :  return lang.errNotFound();
            case NOT_CREATED :  return lang.errNotCreated();
            case NOT_UPDATED :  return lang.errNotUpdated();
            case NOT_REMOVED :  return lang.errNotRemoved();
            case NOT_AVAILABLE:  return lang.errNotAvailable();
            case UNDEFINED_OBJECT: return lang.errUndefinedObject();
            case ALREADY_EXIST :  return lang.errAlreadyExist();
            case ALREADY_EXIST_RELATED: return lang.errAlreadyExistRelated();
            case VALIDATION_ERROR: return lang.errValidationError();
            case INCORRECT_PARAMS: return lang.errIncorrectParams();
            case DB_COMMON_ERROR: return  lang.errDatabaseError();
            case DB_TEMP_ERROR: return lang.errDatabaseTempError();
            case PERMISSION_DENIED: return lang.errPermissionDenied();
            case SESSION_NOT_FOUND: return lang.errSessionNotFound();
            case INVENTORY_NUMBER_ALREADY_EXIST: return lang.errInventoryNumberAlreadyExist();
            case DECIMAL_NUMBER_ALREADY_EXIST: return lang.errDecimalNumberAlreadyExist();
            case PROHIBITED_PRIVATE_COMMENT: return lang.errIssueCommentProhibitedPrivate();
            case INVALID_CASE_UPDATE_CASE_IS_CLOSED: return lang.errInvalidCaseUpdateCaseIsClosed();
            case INVALID_CURRENT_PASSWORD: return lang.errInvalidCurrentPassword();
            case NOT_ALLOWED_CHANGE_ISSUE_NAME_OR_DESCRIPTION: return lang.errNotAllowedChangeIssueNameOrDescription();
            case NOT_ALLOWED_CHANGE_PLATFORM_COMPANY: return lang.errSavePlatformConnectedIssuesExist();
            case NOT_ALLOWED_CHANGE_PROJECT_COMPANY: return lang.errSaveProjectCannotChangeCompany();
            case NOT_ALLOWED_LINK_ISSUE_TO_ITSELF: return lang.errUnableLinkIssueToItself();
            case THIS_LINK_ALREADY_ADDED: return lang.errCaseLinkAlreadyAdded();
            case NOT_ALLOWED_REMOVE_USED_DOCUMENT_TYPE: return lang.documentTypeUnableToRemoveUsedDocumentType();
            case UPDATE_OR_REMOVE_LINKED_OBJECT_ERROR: return lang.errUpdateOrDeleteLinkedObjectError();
            case SVN_ERROR: return lang.errSVN();
            case WORKER_WITH_THIS_DEPARTMENT_ALREADY_EXIST: return lang.errWorkerWithThisDepartmentAlreadyExist();
            case WORKER_WITH_THIS_POSITION_ALREADY_EXIST: return lang.errWorkerWithThisPositionAlreadyExist();
            case DEPARTMENT_ALREADY_EXIST: return lang.errDepartmentAlreadyExistInThisCompany();
            case POSITION_ALREADY_EXIST: return lang.errPositionAlreadyExistInThisCompany();
            case EMPLOYEE_ALREADY_EXIST: return lang.errEmployeeAlreadyExist();
            case EMPLOYEE_EMAIL_ALREADY_EXIST: return lang.errEmployeeEmailAlreadyExist();
            case EMPLOYEE_NOT_FIRED_FROM_THESE_COMPANIES: return lang.errEmployeeNotFiredFromTheseCompanies();
            case EMPLOYEE_MIGRATION_FAILED: return lang.errEmployeeMigrationFailed();
            case LOGIN_ALREADY_EXIST: return lang.errLoginAlreadyExist();
            case SUBNET_DOES_NOT_EXIST: return lang.errSaveReservedIpSubnetDoesNotExist();
            case SUBNET_NOT_ALLOWED_FOR_RESERVE: return lang.errSaveReservedIpSubnetNotAvailable();
            case ROOM_RESERVATION_FINISHED: return lang.errRoomReservationFinished();
            case ROOM_RESERVATION_ROOM_NOT_ACCESSIBLE: return lang.errRoomReservationRoomNotAccessible();
            case ROOM_RESERVATION_HAS_INTERSECTIONS: return lang.errRoomReservationHasIntersections();
            case ISSUE_FILTER_IS_USED: return lang.errPersonCaseFilterInUse();
            case REQUEST_1C_FAILED: return lang.errRequest1CFailed();
            case ABSENCE_HAS_INTERSECTIONS: return lang.errAbsenceHasIntersections();
            case NOT_CURRENT_ABSENCE: return lang.errNotCurrentAbsence();
            case YOUTRACK_SYNCHRONIZATION_FAILED: return lang.errYoutrackSynchronizationFailed();
            case PROJECT_NOT_SELECTED: return lang.errContractProjectRequired();
            case CONTRACTOR_NOT_REMOVED_HAS_CONTRACTS: return lang.errContractorNotRemovedHasContracts();
            case NOT_ALLOWED_EDIT_COMMENT_BY_TIME: return lang.errEditIssueCommentByTime();
            case NOT_ALLOWED_REMOVE_COMMENT_BY_TIME: return lang.errRemoveIssueCommentByTime();
            case REPORTING_SERVICE_NOT_CONFIGURED: return lang.errReportingServiceNotConfigured();
            case NRPE_NOT_CONFIGURED: return lang.errNRPEIpNotConfigured();
            case NRPE_IP_NON_AVAILABLE: return lang.errNRPEIpNonAvailable();
            case NRPE_ERROR: return lang.errNRPEError();
            case NRPE_NO_FREE_IPS: return lang.errNRPENoFreeIps();
            case USER_NOT_FOUND: return lang.errUserNotFound();
            case NOT_FOUND_PARENT: return lang.errNotFoundParent();
            case INVALID_CASE_UPDATE_SUBTASK_NOT_CLOSED: return lang.errInvalidCaseUpdateSubtaskNotClosed();
            case NOT_ALLOWED_PARENT_STATE: return lang.errNotAllowedParentState();
            case NOT_ALLOWED_AUTOOPEN_ISSUE: return lang.errNotAllowedCompanyWithAutoOpenIssue();
            case NOT_ALLOWED_INTEGRATION_ISSUE: return lang.errNotAllowedIntegrationIssue();
            case MYSQL_DATA_TRUNCATION: return lang.errMySqlDataTruncation();
            case INVALID_FILE_FORMAT: return lang.errInvalidFileFormat();
            case NOT_AVAILABLE_DELIVERY_KIT_SERIAL_NUMBER: return lang.errDeliverySerialNumberNotAvailable();
            case NOT_AVAILABLE_MODULE_SERIAL_NUMBER: return lang.errModuleSerialNumberNotAvailable();
            case KIT_SERIAL_NUMBER_NOT_MATCH_DELIVERY_NUMBER: return lang.errKitSerialNumberNotMatchDeliveryNumber();
            case DELIVERY_FORBIDDEN_CHANGE_STATUS: return lang.errDeliveryForbiddenChangeStatus();
            case DELIVERY_FORBIDDEN_CHANGE_PROJECT: return lang.errDeliveryForbiddenChangeProject();
            case CARD_BATCH_HAS_CARD: return lang.errCardBatchForbiddenRemove();
            case ORGANIZATION_REQUIRED: return lang.errOrganizationRequired();
            default: return lang.errUnknownResult();
        }
    }

    Lang lang;
}
