package ru.protei.portal.core.nrpe.response;

import ru.protei.portal.core.nrpe.NRPEResponse;
import ru.protei.portal.core.nrpe.NRPEStatus;

public class NRPEIncorrectParams implements NRPEResponse {
    private final String message;

    public NRPEIncorrectParams(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public NRPEStatus getNRPEStatus() {
        return NRPEStatus.INCORRECT_PARAMS;
    }
}
