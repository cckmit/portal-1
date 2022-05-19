package ru.protei.portal.core.report.transportationrequest;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.TransportationRequestQuery;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Predicate;

public interface ReportTransportationRequest {
    boolean writeReport(OutputStream buffer,
                        Report report,
                        TransportationRequestQuery query,
                        Predicate<Long> isCancel) throws IOException;
}
