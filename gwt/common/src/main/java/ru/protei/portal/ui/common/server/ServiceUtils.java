package ru.protei.portal.ui.common.server;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;

public class ServiceUtils {
    /**
     * Проверка статуса результата
     *
     * @param result Результат запроса на сервис
     * @throws RequestFailedException  если статус не Status.OK
     */
    public static void checkResult(CoreResponse result) throws RequestFailedException {
        if (result != null && result.isOk()) {
            return;
        }

        if (result == null) {
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        throw new RequestFailedException(result.getStatus());
    }

    public static <T> T checkResultAndGetData(CoreResponse<T> result) throws RequestFailedException {
        checkResult(result);
        return result.getData();
    }

    public static UserSessionDescriptor getDescriptor(SessionService sessionService, HttpServletRequest httpRequest) throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpRequest);
        if (descriptor == null) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }
        return descriptor;
    }

    public static AuthToken getAuthToken(SessionService sessionService, HttpServletRequest httpRequest) throws RequestFailedException {
        AuthToken authToken = getDescriptor(sessionService, httpRequest).makeAuthToken();
        if (authToken == null) {
            throw new RequestFailedException( En_ResultStatus.INTERNAL_ERROR );
        }
        return authToken;
    }
}
