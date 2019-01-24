package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.EmployeeRegistrationEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by michael on 26.05.17.
 */
public class CaseSubscriptionServiceImpl implements CaseSubscriptionService {

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    ProductSubscriptionDAO productSubscriptionDAO;

    @Autowired
    CompanyGroupHomeDAO companyGroupHomeDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    PortalConfig portalConfig;

    private Set<NotificationEntry> employeeRegistrationEventSubscribers = new HashSet<>();

    @PostConstruct
    private void parseEmployeeRegistrationRecipients() {
        String[] recipientEmails = portalConfig.data().getMailNotificationConfig().getCrmEmployeeRegistrationNotificationsRecipients();

        for (String recipientEmail : recipientEmails) {
            NotificationEntry notificationEntry = new NotificationEntry(recipientEmail, En_ContactItemType.EMAIL, "ru");
            employeeRegistrationEventSubscribers.add(notificationEntry);
        }
    }


    @Override
    public Set<NotificationEntry> subscribers(AssembledCaseEvent event) {
        return getByCase(event.getCaseObject());
    }

    @Override
    public Set<NotificationEntry> subscribers(EmployeeRegistrationEvent event) {
        HashSet<NotificationEntry> notifiers = new HashSet<>(employeeRegistrationEventSubscribers);
        Optional.ofNullable(event.getEmployeeRegistration())
                .map(EmployeeRegistration::getCreatorId)
                .map(personDAO::get)
                .map(Person::getContactInfo)
                .map(contactInfo -> new PlainContactInfoFacade(contactInfo).getEmail())
                .map(email -> new NotificationEntry(email, En_ContactItemType.EMAIL, "ru"))
                .ifPresent(notifiers::add);
        return notifiers;
    }

    private Set<NotificationEntry> getByCase (CaseObject caseObject){
        Set<NotificationEntry> result = new HashSet<>();
        appendCompanySubscriptions(caseObject.getInitiatorCompanyId(), result);
        appendProductSubscriptions(caseObject.getProductId(), result);
        if(caseObject.getNotifiers() != null){
            for(Person notifier: caseObject.getNotifiers()){
                ContactItem email = notifier.getContactInfo().findFirst(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC);
                if(email == null)
                    continue;
                result.add(NotificationEntry.email(email.value(), "ru"));
            }
        }
        //HomeCompany persons don't need to get notifications
//        companyGroupHomeDAO.getAll().forEach( hc -> appendCompanySubscriptions(hc.getCompanyIds(), result));
        return result;
    }

    private List<CompanySubscription> safeGetByCompany( Long companyId ) {
        if (companyId == null) return Collections.emptyList();
        Company company = companyDAO.get( companyId );
        if (company == null) return Collections.emptyList();

        List<CompanySubscription> selfSubscriptions = companySubscriptionDAO.listByCompanyId( companyId );
        if (company.getParentCompanyId() != null)
            selfSubscriptions.addAll( companySubscriptionDAO.listByCompanyId( company.getParentCompanyId() ) );
        return selfSubscriptions;
    }

    private List<DevUnitSubscription> safeGetByDevUnit(Long devUnitId) {
        return devUnitId == null ? Collections.emptyList() : productSubscriptionDAO.listByDevUnitId(devUnitId);
    }

    private void appendCompanySubscriptions(Long companyId, Set<NotificationEntry> result) {
        List<CompanySubscription> companySubscriptions = safeGetByCompany( companyId );
        log.info( "appendCompanySubscriptions(): {}", companySubscriptions.stream().map(smsc->smsc.getEmail()).collect( Collectors.joining( "," ) ) );
        companySubscriptions.forEach(s -> result.add(NotificationEntry.email(s.getEmail(), s.getLangCode())));
    }

    private static final Logger log = LoggerFactory.getLogger( CaseSubscriptionServiceImpl.class );
    private void appendProductSubscriptions( Long devUnitId, Set<NotificationEntry> result ) {
        safeGetByDevUnit(devUnitId)
                .forEach(s -> result.add(NotificationEntry.email(s.getEmail(), s.getLangCode())));
    }
}
