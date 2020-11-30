package ru.protei.portal.core.service.template;

import freemarker.template.TemplateException;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.LinkData;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Сервис формирования шаблонов
 */
public interface TemplateService {
    PreparedTemplate getCrmEmailNotificationBody(
            AssembledCaseEvent caseObject, List<CaseComment> caseComments, Collection<Attachment> attachments,
            DiffCollectionResult<LinkData> mergeLinks, String urlTemplate, Collection<String> recipients, EnumLangUtil enumLangUtil);

    PreparedTemplate getCrmEmailNotificationSubject( AssembledCaseEvent event, Person currentPerson );

    PreparedTemplate getEmployeeRegistrationEmailNotificationBody(AssembledEmployeeRegistrationEvent event, String urlTemplate, Collection<String> recipients);

    PreparedTemplate getEmployeeRegistrationEmailNotificationSubject( EmployeeRegistration employeeRegistration);

    PreparedTemplate getUserLoginNotificationBody( UserLoginUpdateEvent event, String url);

    PreparedTemplate getUserLoginNotificationSubject( String url);

    PreparedTemplate getContractRemainingOneDayNotificationBody(Contract contract, ContractDate contractDate, String urlTemplate, Collection<String> recipients, EnumLangUtil enumLangUtil);

    PreparedTemplate getContractRemainingOneDayNotificationSubject(Contract contract, ContractDate contractDate, EnumLangUtil enumLangUtil);

    PreparedTemplate getDocumentMemberAddedBody(String documentName, String url);

    PreparedTemplate getDocumentMemberAddedSubject(String documentName);

    PreparedTemplate getDocumentDocFileUpdatedByMemberBody(String documentName, String initiatorName, String comment);

    PreparedTemplate getDocumentDocFileUpdatedByMemberSubject(String documentName);

    PreparedTemplate getMailReportBody(ReportDto reportDto, Interval createdInterval, Interval modifiedInterval);

    PreparedTemplate getMailReportSubject(ReportDto reportDto);

    String getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationBody( Long employeeRegistrationId, String employeeFullName, String urlTemplate, String recipientName ) throws IOException, TemplateException;

    String getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationSubject( String employeeFullName ) throws IOException, TemplateException;

    String getEmployeeRegistrationProbationCuratorsEmailNotificationBody( Long employeeRegistrationId, String employeeFullName, String urlTemplate, String recipientName ) throws IOException, TemplateException;

    String getEmployeeRegistrationProbationCuratorsEmailNotificationSubject( String employeeFullName ) throws IOException, TemplateException;

    String getEmployeeRegistrationDevelopmentAgendaEmailNotificationBody( String employeeName ) throws IOException, TemplateException;

    String getEmployeeRegistrationEmployeeFeedbackEmailNotificationBody( String employeeName ) throws IOException, TemplateException;

    String getEmployeeRegistrationEmployeeFeedbackEmailNotificationSubject() throws IOException, TemplateException;

    String getEmployeeRegistrationDevelopmentAgendaEmailNotificationSubject() throws IOException, TemplateException;

    PreparedTemplate getMailProjectSubject(Project project, Person initiator);

    PreparedTemplate getMailProjectBody(AssembledProjectEvent event, List<CaseComment> comments, Collection<String> recipients, DiffCollectionResult<LinkData> links, String crmProjectUrl, EnumLangUtil roleTypeLang);

    PreparedTemplate getRoomReservationNotificationSubject(RoomReservation roomReservation, RoomReservationNotificationEvent.Action action);

    PreparedTemplate getRoomReservationNotificationBody(RoomReservation roomReservation, RoomReservationNotificationEvent.Action action, Collection<String> recipients);

    PreparedTemplate getSubnetNotificationSubject(Subnet subnet, Person initiator, SubnetNotificationEvent.Action action);

    PreparedTemplate getSubnetNotificationBody(Subnet subnet, SubnetNotificationEvent.Action action, Collection<String> recipients);

    PreparedTemplate getReservedIpNotificationSubject(List<ReservedIp> reservedIps, Person initiator, ReservedIpNotificationEvent.Action action);

    PreparedTemplate getReservedIpNotificationBody(List<ReservedIp> reservedIps, Collection<String> recipients);

    PreparedTemplate getReservedIpRemainingNotificationSubject(Date releaseDateStart, Date releaseDateEnd);

    PreparedTemplate getPersonCaseFilterNotificationSubject();

    PreparedTemplate getPersonCaseFilterNotificationBody(List<CaseObject> issues, String urlTemplate);

    PreparedTemplate getAbsenceNotificationSubject(Person initiator, PersonAbsence absence);

    PreparedTemplate getAbsenceNotificationBody(AbsenceNotificationEvent event, EventAction action, Collection<String> recipients);

    PreparedTemplate getAbsenceReportSubject(String title);

    PreparedTemplate getDutyLogReportSubject(String title);
    PreparedTemplate getReportBody(String title, Date creationDate, String creator, List<String> recipients);

    String getProjectPauseTimeNotificationSubject( Long projectNumber, String projectName ) throws IOException, TemplateException;
    String getProjectPauseTimeNotificationBody( String subscriberName, Long aLong, String displayNam, String projectUrl, Date pauseTimeDate ) throws IOException, TemplateException;

    PreparedTemplate getBirthdaysNotificationSubject(Date from, Date to);
    PreparedTemplate getBirthdaysNotificationBody(List<EmployeeShortView> employees, Collection<String> recipients);

    PreparedTemplate getNRPENonAvailableIpsNotificationSubject();
    PreparedTemplate getNRPENonAvailableIpsNotificationBody(List<String> nonAvailableIps, Collection<String> recipients);

    PreparedTemplate getExpiringTechnicalSupportValidityNotificationSubject();
    PreparedTemplate getExpiringTechnicalSupportValidityNotificationBody(ExpiringProjectTSVNotificationEvent event,
                                     Collection<String> recipients, String urlTemplate);
}
