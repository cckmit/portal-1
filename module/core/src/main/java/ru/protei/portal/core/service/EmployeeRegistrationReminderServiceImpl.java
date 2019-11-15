package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.EmployeeRegistrationDevelopmentAgendaEvent;
import ru.protei.portal.core.event.EmployeeRegistrationEmployeeFeedbackEvent;
import ru.protei.portal.core.event.EmployeeRegistrationProbationCuratorsEvent;
import ru.protei.portal.core.event.EmployeeRegistrationProbationHeadOfDepartmentEvent;
import ru.protei.portal.core.model.dao.EmployeeRegistrationDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.events.EventPublisherService;

import java.util.*;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.join;

public class EmployeeRegistrationReminderServiceImpl implements EmployeeRegistrationReminderService {

    @Override
    public Result<Boolean> notifyAboutEmployeeFeedback() {
        List<EmployeeRegistration> probationComplete = employeeRegistrationDAO.getAfterProbationList( SEND_EMPLOYEE_FEEDBACK_AFTER_PROBATION_END_DAYS );
        log.info( "notifyAboutEmployeeFeedback(): {}", toList( probationComplete, EmployeeRegistration::getId ) );

        for (EmployeeRegistration employeeRegistration : emptyIfNull( probationComplete )) {
            if (employeeRegistration.getPerson() == null) continue;

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

            addCaseComment( employeeRegistration.getId(), message.toString() );
        }

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
        Result<Long> commentId = caseCommentService.addCommentOnSentReminder(comment);

        if (!commentId.isOk()) {
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

    private Map<Long, Person> collectPersonsForNotification( List<EmployeeRegistration> probationExpires ) {
        Set<Long> notifyIds = new HashSet<Long>();

        for (EmployeeRegistration er : emptyIfNull( probationExpires )) {
            notifyIds.add( er.getHeadOfDepartmentId() );
            notifyIds.addAll( emptyIfNull( er.getCuratorsIds() ) );
        }

        List<Person> persons = personDAO.partialGetListByKeys( notifyIds, "id", "displayname", "contactInfo" );
        return toMap( persons, Person::getId, person -> person );
    }

    private static final Logger log = LoggerFactory.getLogger( EmployeeRegistrationReminderServiceImpl.class );


    private String getLangFor(String key){
        return langRu.getString( key );
    }


    @Autowired
    EmployeeRegistrationDAO employeeRegistrationDAO;
    @Autowired
    CaseCommentService caseCommentService;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    EventPublisherService publisherService;

    ResourceBundle langRu = ResourceBundle.getBundle("Lang", new Locale( "ru", "RU"));

    public static final int SEND_EMPLOYEE_FEEDBACK_AFTER_PROBATION_END_DAYS = 1;
    public static final int SEND_AGENDA_TO_PROBATION_END_DAYS = 7;
    public static final int SEND_PROBATION_EXPIRES_TO_PROBATION_END_DAYS = 9;
}
