package ru.protei.portal.core.report.contract;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ContractQuery;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.function.Predicate;

public interface ReportContract {
    boolean writeReport(OutputStream buffer,
                        Report report,
                        ContractQuery query,
                        DateFormat dateFormat,
                        Predicate<Long> isCancel) throws IOException;
}
