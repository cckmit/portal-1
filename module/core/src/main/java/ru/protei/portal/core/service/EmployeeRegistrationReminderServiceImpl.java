package ru.protei.portal.core.service;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.EmployeeRegistrationDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.join;

public class EmployeeRegistrationReminderServiceImpl implements EmployeeRegistrationReminderService {
    private static final Logger log = LoggerFactory.getLogger( EmployeeRegistrationReminderServiceImpl.class );

    public static final int SEND_EMPLOYEE_FEEDBACK_AFTER_PROBATION_END_DAYS = 1;
    public static final int SEND_AGENDA_TO_PROBATION_END_DAYS = 7;
    public static final int SEND_PROBATION_EXPIRES_TO_PROBATION_END_DAYS = 9;

    @Autowired
    EmployeeRegistrationDAO employeeRegistrationDAO;
    @Autowired
    CaseCommentService caseCommentService;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    EventPublisherService publisherService;

    ResourceBundle langRu = ResourceBundle.getBundle("Lang", new Locale( "ru", "RU"));

    @Override
    public Result<Boolean> notifyAboutEmployeeFeedback() {
        List<EmployeeRegistration> probationComplete = employeeRegistrationDAO.getAfterProbationList( SEND_EMPLOYEE_FEEDBACK_AFTER_PROBATION_END_DAYS );
        log.info( "notifyAboutEmployeeFeedback(): {}", toList( probationComplete, EmployeeRegistration::getId ) );

        for (EmployeeRegistration employeeRegistration : emptyIfNull( probationComplete )) {
            if (employeeRegistration.getPerson() == null) continue;
            jdbcManyRelationsHelper.fill(employeeRegistration.getPerson(), Person.Fields.CONTACT_ITEMS);
            notifyEmployerAboutFeedback( employeeRegistration.getPerson() );
            addCaseComment( employeeRegistration.getId(), makeEmployeeFeedbackComment( employeeRegistration.getPerson() ) );
        }

        return ok(true );
    }


    @Override
    public Result<Boolean> notifyAboutDevelopmentAgenda() {
        List<EmployeeRegistration> probationExpires = employeeRegistrationDAO.getProbationExpireList( SEND_AGENDA_TO_PROBATION_END_DAYS );
        log.info( "notifyAboutDevelopmentAgenda(): {}", toList( probationExpires, EmployeeRegistration::getId ) );

        for (EmployeeRegistration employeeRegistration : emptyIfNull( probationExpires )) {
            if (employeeRegistration.getPerson() == null) continue;
            jdbcManyRelationsHelper.fill(employeeRegistration.getPerson(), Person.Fields.CONTACT_ITEMS);
            notifyEmployerAboutAgenda( employeeRegistration.getPerson() );
            addCaseComment( employeeRegistration.getId(), makeDevelopmentAgendaComment( employeeRegistration.getPerson() ) );
        }

        return ok(true );
    }

    @Override
    public Result<Boolean> notifyAboutProbationPeriod() {
        List<EmployeeRegistration> probationExpires = employeeRegistrationDAO.getProbationExpireList( SEND_PROBATION_EXPIRES_TO_PROBATION_END_DAYS );
        log.info( "notifyAboutProbationPeriod(): {}", toList (probationExpires, EmployeeRegistration::getId ) );

        Map<Long, Person> idToPerson = collectPersonsForNotification( probationExpires );

        for (EmployeeRegistration employeeRegistration : emptyIfNull( probationExpires )) {
            Person headOfDepartment = idToPerson.get( employeeRegistration.getHeadOfDepartmentId() );
            String employeeFullName = employeeRegistration.getEmployeeFullName();
            Long employeeId = employeeRegistration.getId();

            notifyHeadOfDepartment( headOfDepartment, employeeFullName, employeeId );
            StringBuilder message = makeProbationComment( employeeRegistration.getHeadOfDepartmentShortName() );

            for (Long curatorId : emptyIfNull( employeeRegistration.getCuratorsIds() )) {
                Person curator = idToPerson.get( curatorId );

                notifyEmployeeCurator( curator, employeeFullName, employeeId );
                message = join( message, ", ", curator.getDisplayName() );
            }

            List<String> recipients = collectAdditionalRecipients( employeeRegistration );
            notifyAdditionalRecipients( recipients, employeeFullName, employeeId );

            for (String recipient : recipients) {
                message = join(message, ", ", recipient);
            }

            addCaseComment( employeeRegistration.getId(), message.toString() );
        }

        return ok(true );
    }

    @Override
    public Result<Boolean> notifyAboutEmployeeProbationPeriod(EmployeeRegistration employeeRegistration) {
        log.info( "notifyAboutProbationPeriod(): {}", employeeRegistration.getId() );

        Person headOfDepartment = personDAO.get( employeeRegistration.getHeadOfDepartmentId() );
        jdbcManyRelationsHelper.fill( headOfDepartment, Person.Fields.CONTACT_ITEMS );
        String employeeFullName = employeeRegistration.getEmployeeFullName();
        Long employeeId = employeeRegistration.getId();

        notifyHeadOfDepartment( headOfDepartment, employeeFullName, employeeId );
        StringBuilder message = makeProbationComment( employeeRegistration.getHeadOfDepartmentShortName() );

        List<Person> personList = personDAO.partialGetListByKeys( employeeRegistration.getCuratorsIds(), "id", "displayname", "company_id" );
        jdbcManyRelationsHelper.fill( personList, Person.Fields.CONTACT_ITEMS );

        for (Person curator : emptyIfNull( personList )) {
            notifyEmployeeCurator(curator, employeeFullName, employeeId);
            message = join(message, ", ", curator.getDisplayName());
        }

        List<String> recipients = collectAdditionalRecipients( employeeRegistration );
        notifyAdditionalRecipients( recipients, employeeFullName, employeeId );

        for (String recipient : recipients) {
            message = join(message, ", ", recipient);
        }

        addCaseComment( employeeRegistration.getId(), message.toString() );

        return ok(true );
    }


    @Override
    public Result<Boolean> notifyEmployeeAboutDevelopmentAgenda(EmployeeRegistration employeeRegistration) {
        if (employeeRegistration.getPerson() == null) return ok(true);

        Person employee = employeeRegistration.getPerson();
        jdbcManyRelationsHelper.fill(employee, Person.Fields.CONTACT_ITEMS);

        notifyEmployerAboutAgenda( employee );
        addCaseComment( employeeRegistration.getId(), makeDevelopmentAgendaComment( employee ) );

        return ok(true );
    }


    private StringBuilder makeProbationComment( String headOfDepartmentName ) {
        return join( getLangFor( "sent_reminder_about_response" ), "\n",
                getLangFor( "reminder_recipients" ), ": ", headOfDepartmentName );
    }

    private String makeDevelopmentAgendaComment( Person person ) {
        return join( getLangFor( "send_reminder_about_development_agenda" ), "\n",
                getLangFor( "reminder_recipients" ), ": ", person.getDisplayShortName() ).toString();
    }

    private String makeEmployeeFeedbackComment( Person person ) {
        return join( getLangFor( "sent_reminder_about_employee_feedback" ), "\n",
                getLangFor( "reminder_recipients" ), ": ", person.getDisplayShortName() ).toString();
    }

    private void addCaseComment( Long caseId, String message ) {
        CaseComment comment = new CaseComment(message);
        comment.setCaseId( caseId );
        comment.setOriginalAuthorName( getLangFor("reminder_system_name") );
        comment.setPrivacyType( En_CaseCommentPrivacyType.PUBLIC );
        Result<Long> commentId = caseCommentService.addCommentOnSentReminder(comment);

        if (commentId.isError()) {
            log.warn( "addCaseComment(): Can't add case comment about {} for caseId={}",  message, caseId  );
        }
    }

    private void notifyEmployerAboutFeedback( Person employee ) {
        publisherService.publishEvent( new EmployeeRegistrationEmployeeFeedbackEvent( this,
                employee ) );
    }

    private void notifyEmployerAboutAgenda( Person employee ) {
        publisherService.publishEvent( new EmployeeRegistrationDevelopmentAgendaEvent( this,
                employee ) );
    }

    private void notifyEmployeeCurator( Person curator, String employeeFullName, Long employeeId ) {
        publisherService.publishEvent( new EmployeeRegistrationProbationCuratorsEvent( this,
                curator, employeeFullName, employeeId  ) );
    }

    private void notifyHeadOfDepartment( Person headOfDepartment, String employeeFullName, Long employeeId ) {
        publisherService.publishEvent( new EmployeeRegistrationProbationHeadOfDepartmentEvent( this,
                headOfDepartment, employeeFullName, employeeId ) );
    }

    private void notifyAdditionalRecipients(List<String> recipients, String employeeFullName, Long employeeId) {
        publisherService.publishEvent( new EmployeeRegistrationProbationAdditionalRecipientsEvent( this,
                recipients, employeeFullName, employeeId ) );
    }

    private List<String> collectAdditionalRecipients(EmployeeRegistration employeeRegistration) {
        Company company = companyDAO.get(employeeRegistration.getCompanyId());
        if (company == null) return new ArrayList<>();
        jdbcManyRelationsHelper.fill(company, Company.Fields.CONTACT_ITEMS);

        return stream(company.getContactInfo().getItems(En_ContactItemType.EMAIL))
                .filter(ContactItem::isSubscribedToTheEndOfProbation)
                .map(ContactItem::value)
                .filter(Strings::isNotEmpty)
                .collect(Collectors.toList());
    }

    private Map<Long, Person> collectPersonsForNotification( List<EmployeeRegistration> probationExpires ) {
        Set<Long> notifyIds = new HashSet<Long>();

        for (EmployeeRegistration er : emptyIfNull( probationExpires )) {
            notifyIds.add( er.getHeadOfDepartmentId() );
            notifyIds.addAll( emptyIfNull( er.getCuratorsIds() ) );
        }

        List<Person> persons = personDAO.partialGetListByKeys( notifyIds, "id", "displayname", "company_id");
        jdbcManyRelationsHelper.fill(persons, Person.Fields.CONTACT_ITEMS);
        return toMap( persons, Person::getId, person -> person );
    }

    private String getLangFor(String key){
        return langRu.getString( key );
    }
}
