package ru.protei.portal.core.service;

import ru.protei.portal.core.event.AssembledCaseEvent;

public interface AssemblerService {
    void proceed( AssembledCaseEvent assembledEvent );
}
