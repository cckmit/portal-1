package ru.protei.portal.core.report.transportationrequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.DateRangeUtils;
import ru.protei.portal.core.model.query.TransportationRequestQuery;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.tools.ChunkIterator;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;

public class ReportTransportationRequestImpl implements ReportTransportationRequest {

    private static Logger log = LoggerFactory.getLogger(ReportTransportationRequestImpl.class);

    @Autowired
    private Lang lang;
    @Autowired
    private PortalConfig config;
    @Autowired
    private YoutrackApi youtrackApi;

    @Override
    public boolean writeReport(OutputStream buffer,
                               Report report,
                               TransportationRequestQuery query,
                               Predicate<Long> isCancel) throws IOException {
        if (!query.isParamsPresent())
            return false;

        Interval pickupDate = DateRangeUtils.makeInterval(query.getPickupDate());
        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));

        log.debug("writeReport : reportId={} to process", report.getId());
        log.debug("writeReport : request data from yt");
        //        запрос производится для проекта DLVRY за период по датам получения для оператора "Тройка"
        String ytQuery = String.format("проект: %s Оператор: %s Дата получения: %s .. %s", "Отправка", "Тройка",
                dateToYtString(pickupDate.getFrom()),  dateToYtString(pickupDate.getTo()));
        ChunkIterator<YtIssue> ytIterator = new ChunkIterator<>(
                (offset, limit) -> youtrackApi.getIssueWithFieldsByQuery(ytQuery, offset, limit),
                () -> isCancel.test(report.getId()),
                config.data().reportConfig().getChunkSize()
        );

        switch(ytIterator.getStatus()) {
            case OK: { log.debug("writeReport : collect yt data with isOk status : reportId={}", report.getId()); break; }
            case CANCELED: { log.info("writeReport : yt canceled : reportId={}", report.getId()); return true; }
            default: { log.error("writeReport : yt error : reportId={}, status = {}", report.getId(), ytIterator.getStatus()); return false; }
        }

        try (ExcelReportWriter writer = new ExcelReportWriter(localizedLang)) {
            log.debug("writeReport : start write sheet");
            int sheetNumber = writer.createSheet();
            ytIterator.forEachRemaining(ytIssue -> writer.write(sheetNumber, ytIssue));
            writer.collect(buffer);
            log.debug("writeReport : reportId={} to end", report.getId());
            return true;
        } catch (Throwable th) {
            log.error("writeReport : fail to write : reportId={}, th={}", report.getId(), th);
            return false;
        }
    }

    private String dateToYtString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
