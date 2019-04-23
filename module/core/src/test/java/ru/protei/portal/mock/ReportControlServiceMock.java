package ru.protei.portal.mock;

import ru.protei.portal.core.service.ReportControlServiceImpl;

/**
 * Disable automated report processing
 */
public class ReportControlServiceMock extends ReportControlServiceImpl {

    @Override
    public void init() {}

    @Override
    public void processNewReportsSchedule() {}

    @Override
    public void processOldReportsSchedule() {}

    @Override
    public void processHangReportsSchedule() {}
}
