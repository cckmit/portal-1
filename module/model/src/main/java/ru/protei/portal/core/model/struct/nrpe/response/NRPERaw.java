package ru.protei.portal.core.model.struct.nrpe.response;

import java.util.List;

public abstract class NRPERaw implements NRPEResponse {
    protected List<String> rawResponse;

    public List<String> getRawResponse() {
        return rawResponse;
    }
}
