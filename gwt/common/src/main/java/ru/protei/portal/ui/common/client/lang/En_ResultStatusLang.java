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

        switch (value)
        {
            case OK : return lang.msgOK();
            case CONNECTION_ERROR: return lang.errConnectionError();
            case INTERNAL_ERROR : return lang.errInternalError();
            case INVALID_SESSION_ID :  return lang.errInvalidSessionID();
            case INVALID_LOGIN_OR_PWD :  return lang.errLoginOrPwd();
            case GET_DATA_ERROR: return lang.errGetDataError();
            case NOT_FOUND :  return lang.errNotFound();
            case NOT_CREATED :  return lang.errNotCreated();
            case NOT_UPDATED :  return lang.errNotUpdated();
            case UNDEFINED_OBJECT: return lang.errUndefinedObject();
            case ALREADY_EXIST :  return lang.errAlreadyExist();
            case VALIDATION_ERROR: return lang.errValidationError();
            case INCORRECT_PARAMS: return lang.errIncorrectParams();

            default: return lang.errUnknownResult();
        }
    }

    Lang lang;
}
