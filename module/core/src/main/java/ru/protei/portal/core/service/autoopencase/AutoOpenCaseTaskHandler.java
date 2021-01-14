package ru.protei.portal.core.service.autoopencase;

public interface AutoOpenCaseTaskHandler {

    void runOpenCaseTaskAsync( Long caseId);
    void runOpenCaseTask( Long caseId);
}
