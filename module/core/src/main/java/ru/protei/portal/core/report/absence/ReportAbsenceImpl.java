package ru.protei.portal.core.report.absence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.AbsenceUtils;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ReportAbsenceImpl implements ReportAbsence {

    private static Logger log = LoggerFactory.getLogger(ReportAbsenceImpl.class);

    @Autowired
    Lang lang;

    @Autowired
    PortalConfig config;

    @Autowired
    PersonAbsenceDAO personAbsenceDAO;

    @Override
    public boolean writeReport(OutputStream buffer, final AbsenceQuery query) {

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(CrmConstants.DEFAULT_LOCALE));

        int limit = config.data().reportConfig().getChunkSize();
        int offset = 0;
        try (ReportWriter<PersonAbsence> writer = new ExcelReportWriter(localizedLang, new EnumLangUtil(lang))) {
            int sheetNumber = writer.createSheet();
            writer.setSheetName(sheetNumber, localizedLang.get("ar_absences"));

            while (true) {
                query.setOffset(offset);
                query.setLimit(limit);
                List<PersonAbsence> absences = processChunk(query);
                writer.write(sheetNumber, absences);
                if (size(absences) < limit) break;
                offset += limit;
            }
            writer.collect(buffer);
            return true;
        } catch (Exception ex) {
            log.warn("writeReport : fail to process chunk [{} - {}] : query: {} ", offset, limit, query, ex);
            return false;
        }
    }

    public List<PersonAbsence> processChunk(AbsenceQuery query) {
        List<PersonAbsence> absences = personAbsenceDAO.listByQuery(query);
        if (CollectionUtils.isEmpty(absences)) return new ArrayList<>();

        else return AbsenceUtils.generateAbsencesFromDateRange(absences, query.getDateRange().getFrom(), query.getDateRange().getTo());
    }
}
