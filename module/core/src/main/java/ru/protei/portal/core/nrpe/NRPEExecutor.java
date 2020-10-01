package ru.protei.portal.core.nrpe;

import java.util.List;

public interface NRPEExecutor {
    List<String> execute(String request);
}
