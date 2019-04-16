package ru.protei.portal.mock;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.service.ReportControlService;

public class ReportControlServiceMock implements ReportControlService {

    @Override
    public CoreResponse processNewReports() {
        return new CoreResponse().success();
    }

    @Override
    public CoreResponse processOldReports() {
        return new CoreResponse().success();
    }

    @Override
    public CoreResponse processHangReports() {
        return new CoreResponse().success();
    }

    @Override
    public void processNewReportsSchedule() {}

    @Override
    public void processOldReportsSchedule() {}

    @Override
    public void processHangReportsSchedule() {}
}
