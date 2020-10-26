package ru.protei.portal.core.model.struct.nrpe.response;

import ru.protei.portal.core.model.dict.En_NRPEStatus;

public class NRPEIncorrectParams implements NRPEResponse {
    private final String message;

    public NRPEIncorrectParams(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public En_NRPEStatus getNRPEStatus() {
        return En_NRPEStatus.INCORRECT_PARAMS;
    }
}
