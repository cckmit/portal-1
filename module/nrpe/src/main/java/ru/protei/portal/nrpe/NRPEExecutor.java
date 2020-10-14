package ru.protei.portal.nrpe;

import java.util.List;

public interface NRPEExecutor {
    List<String> execute(String request);
}
