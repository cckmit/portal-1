package ru.protei.portal.core.report.contract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class ReportContractImpl implements ReportContract {

    @Override
    public boolean writeReport(OutputStream buffer,
                               Report report,
                               ContractQuery query,
                               DateFormat dateFormat,
                               Predicate<Long> isCancel) throws IOException {

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));

        int count = contractDAO.countByQuery(query);
        if (count < 1) {
            log.info("writeReport : reportId={} has no corresponding contracts", report.getId());
            ReportWriter<Contract> writer = new ExcelReportWriter(localizedLang, new EnumLangUtil(lang), dateFormat);
            writer.createSheet();
            writer.collect(buffer);
            return true;
        }

        log.info("writeReport : reportId={} has {} contracts to process", report.getId(), count);

        try (ReportWriter<Contract> writer = new ExcelReportWriter(localizedLang, new EnumLangUtil(lang), dateFormat)) {
            int sheetNumber = writer.createSheet();
            if (writeReport(writer, sheetNumber, report.getId(), query, count, isCancel)) {
                writer.collect(buffer);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("writeReport : fail to process reportId={} query: {}", report.getId(), query, e);
            return false;
        }
    }

    private boolean writeReport(ReportWriter<Contract> writer,
                                int sheetNumber,
                                Long reportId,
                                ContractQuery query,
                                int count,
                                Predicate<Long> isCancel) {

        final int step = config.data().reportConfig().getChunkSize();
        final int limit = count;
        int offset = 0;

        while (offset < limit) {
            if (isCancel.test(reportId)) {
                log.info("writeReport(): Cancel processing of report {}", reportId);
                return true;
            }
            int amount = offset + step < limit ? step : limit - offset;
            query.setOffset(offset);
            query.setLimit(amount);
            List<Contract> data = getContracts(query);
            try {
                writer.write(sheetNumber, data);
            } catch (Throwable th) {
                log.error("writeReport : fail to process chunk [{} - {}] : reportId={}", offset, amount, reportId, th);
                return false;
            }
            offset += step;
        }

        return true;
    }

    private List<Contract> getContracts(ContractQuery query) {
        List<Contract> contracts = contractDAO.getSearchResult(query).getResults();
        jdbcManyRelationsHelper.fillAll(contracts);
        return contracts;
    }

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    ReportDAO reportDAO;
    @Autowired
    ContractDAO contractDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    private static Logger log = LoggerFactory.getLogger(ReportContractImpl.class);
}
