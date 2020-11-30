package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.ProcessNewReportsEvent;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.query.DutyLogQuery;

public interface ReportControlService {

    void onProcessNewReportsEvent(ProcessNewReportsEvent event);

    Result<Void> processNewReports();

    Result<Void> processOldReports();

    Result<Void> processScheduledMailReports(En_ReportScheduledType enReportScheduledType);

    /**
     * Запустить формирование отчета по отсутствиям
     */
    Result<Void> processAbsenceReport(Person initiator, String title, AbsenceQuery query);

    /**
     * Запустить формирование отчета по дежурствам
     */
    Result<Void> processDutyLogReport(Person initiator, String title, DutyLogQuery query);
}
