package ru.protei.portal.core.service.report.managertime;

import ru.protei.portal.core.model.ent.Report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReportCrmManagerTimeServiceImpl implements ReportCrmManagerTimeService {

    @Override
    public boolean writeExport(ByteArrayOutputStream buffer, Report report) throws IOException {
        return false;
    }
}
