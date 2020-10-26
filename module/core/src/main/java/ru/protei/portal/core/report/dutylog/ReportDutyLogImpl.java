package ru.protei.portal.core.report.dutylog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.DutyLogDAO;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ReportDutyLogImpl implements ReportDutyLog {

    private static Logger log = LoggerFactory.getLogger(ReportDutyLogImpl.class);

    @Autowired
    Lang lang;

    @Autowired
    PortalConfig config;

    @Autowired
    DutyLogDAO dutyLogDAO;

    @Override
    public boolean writeReport(OutputStream buffer, final DutyLogQuery query) {

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(CrmConstants.DEFAULT_LOCALE));

        int limit = config.data().reportConfig().getChunkSize();
        int offset = 0;
        try (ReportWriter<DutyLog> writer = new ExcelReportWriter(localizedLang, new EnumLangUtil(lang))) {
            int sheetNumber = writer.createSheet();
            writer.setSheetName(sheetNumber, localizedLang.get("dutyLogReport"));

            while (true) {
                query.setOffset(offset);
                query.setLimit(limit);
                List<DutyLog> list = processChunk(query);
                writer.write(sheetNumber, list);
                if (size(list) < limit) break;
                offset += limit;
            }
            writer.collect(buffer);
            return true;
        } catch (Exception ex) {
            log.warn("writeReport : fail to process chunk [{} - {}] : query: {} ", offset, limit, query, ex);
            return false;
        }
    }

    public List<DutyLog> processChunk(DutyLogQuery query) {
        List<DutyLog> dutyLogs = dutyLogDAO.listByQuery(query);
        if (CollectionUtils.isEmpty(dutyLogs)) return new ArrayList<>();
        else return dutyLogs;
    }
}
