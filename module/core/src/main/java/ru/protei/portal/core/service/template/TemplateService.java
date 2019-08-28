package ru.protei.portal.core.service.template;

import freemarker.template.TemplateException;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.UserLoginUpdateEvent;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.template.PreparedTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Сервис формирования шаблонов
 */
public interface TemplateService {
    PreparedTemplate getCrmEmailNotificationBody(
            AssembledCaseEvent caseObject, List< CaseComment > caseComments, String urlTemplate, Collection< String > recipients );

    PreparedTemplate getCrmEmailNotificationSubject( CaseObject caseObject, Person currentPerson );

    PreparedTemplate getEmployeeRegistrationEmailNotificationBody( EmployeeRegistration employeeRegistration, String urlTemplate, Collection<String> recipients);

    PreparedTemplate getEmployeeRegistrationEmailNotificationSubject( EmployeeRegistration employeeRegistration);

    PreparedTemplate getUserLoginNotificationBody( UserLoginUpdateEvent event, String url);

    PreparedTemplate getUserLoginNotificationSubject( String url);

    PreparedTemplate getContractRemainingOneDayNotificationBody(Contract contract, ContractDate contractDate, String urlTemplate, Collection<String> recipients);

    PreparedTemplate getContractRemainingOneDayNotificationSubject(Contract contract, ContractDate contractDate);

    String getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationBody( Long employeeRegistrationId, String employeeFullName, String urlTemplate, String recipientName ) throws IOException, TemplateException;

    String getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationSubject( String employeeFullName ) throws IOException, TemplateException;

    String getEmployeeRegistrationProbationCuratorsEmailNotificationBody( Long employeeRegistrationId, String employeeFullName, String urlTemplate, String recipientName ) throws IOException, TemplateException;

    String getEmployeeRegistrationProbationCuratorsEmailNotificationSubject( String employeeFullName ) throws IOException, TemplateException;

    String getEmployeeRegistrationDevelopmentAgendaEmailNotificationBody( String employeeName ) throws IOException, TemplateException;

    String getEmployeeRegistrationEmployeeFeedbackEmailNotificationBody( String employeeName ) throws IOException, TemplateException;

    String getEmployeeRegistrationEmployeeFeedbackEmailNotificationSubject() throws IOException, TemplateException;

    String getEmployeeRegistrationDevelopmentAgendaEmailNotificationSubject() throws IOException, TemplateException;

}