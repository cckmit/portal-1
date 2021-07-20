package ru.protei.portal.core.service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledDeliveryEvent;
import ru.protei.portal.core.event.AssembledEmployeeRegistrationEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.join;

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
    UserLoginShortViewDAO userLoginShortViewDAO;

    @Autowired
    CaseObjectMetaNotifiersDAO caseObjectMetaNotifiersDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    PersonFavoriteIssuesDAO personFavoriteIssuesDAO;

    @Autowired
    PersonDAO personDAO;

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
    public Set<NotificationEntry> subscribers(AssembledEmployeeRegistrationEvent event) {
        HashSet<NotificationEntry> notifiers = new HashSet<>(employeeRegistrationEventSubscribers);
        Optional.ofNullable(event.getNewState())
                .map(EmployeeRegistration::getCreatorId)
                .map(Person::new)
                .map(person -> {
                    jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
                    return person;
                })
                .map(Person::getContactInfo)
                .map(contactInfo -> new PlainContactInfoFacade(contactInfo).getEmail())
                .map(email -> new NotificationEntry(email, En_ContactItemType.EMAIL, "ru"))
                .ifPresent(notifiers::add);
        log.info( "subscribers: EmployeeRegistrationEvent: {}", join( notifiers, NotificationEntry::getAddress, ",") );
        return notifiers;
    }

    @Override
    public Set<NotificationEntry> subscribers( CaseObjectMeta caseMeta ) {
        Set<NotificationEntry> result = new HashSet<>();
        appendCompanySubscriptions(caseMeta, result);
        appendProductSubscriptions(caseMeta.getProductId(), result);
        appendNotifiers(caseMeta.getId(), result);
        appendNotifiersByFavoriteIssues(caseMeta.getId(), result);
        //HomeCompany persons don't need to get notifications
//        companyGroupHomeDAO.getAll().forEach( hc -> appendCompanySubscriptions(hc.getCompanyIds(), result));
        log.info( "subscribers: AssembledCaseEvent: {}", join( result, NotificationEntry::getAddress, ",") );
        return result;
    }

    @Override
    public Set<NotificationEntry> subscribers(AssembledDeliveryEvent event) {
        Set<NotificationEntry> result = new HashSet<>();
        appendNotifiers(event.getDeliveryId(), result);
        Project project = event.getNewDeliveryState().getProject();

        //инициатор оповещения
        Long initiatorId = event.getInitiatorId();
        //ответственный менеджер
        Long managerId = project == null ? null : project.getManagerId();
        //ответственный АО
        Long hwManagerId = event.getNewDeliveryState().getHwManagerId();
        //ответственный КК
        Long qcManagerId = event.getNewDeliveryState().getQcManagerId();
        //контактное лицо
        Long contactPersonId = event.getNewDeliveryState().getInitiatorId();
        result.addAll(subscribers(Arrays.asList(initiatorId, managerId, hwManagerId, qcManagerId, contactPersonId)));
        log.info( "Delivery subscribers: {}", join( result, NotificationEntry::getAddress, ",") );
        return result;
    }

    @Override
    public Set<NotificationEntry> subscribers(List<Long> personIds) {
        List<Person> persons = personDAO.partialGetListByKeys(personIds, "id","locale");
        jdbcManyRelationsHelper.fill(persons, Person.Fields.CONTACT_ITEMS);
        return stream(persons)
                .filter(Objects::nonNull)
                .map(person -> {
                    PlainContactInfoFacade contact = new PlainContactInfoFacade(person.getContactInfo());
                    return NotificationEntry.email(contact.getEmail(), person.getLocale());
                })
                .filter(entry -> StringUtils.isNotEmpty(entry.getAddress()))
                .collect(Collectors.toSet());
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

    private void appendCompanySubscriptions(CaseObjectMeta caseMeta, Set<NotificationEntry> result) {
        List<CompanySubscription> companySubscriptions = safeGetByCompany(caseMeta.getInitiatorCompanyId());
        List<CompanySubscription> managerCompanySubscriptions = safeGetByCompany(caseMeta.getManagerCompanyId());

        Set<CompanySubscription> allCompanySubscriptions = new HashSet<>();
        allCompanySubscriptions.addAll(companySubscriptions);
        allCompanySubscriptions.addAll(managerCompanySubscriptions);

        List<CompanySubscription> subscriptionsBasedOnPlatformAndProduct = filterByPlatformAndProduct(allCompanySubscriptions, caseMeta.getPlatformId(), caseMeta.getProductId());

        subscriptionsBasedOnPlatformAndProduct.forEach(s -> result.add(NotificationEntry.email(s.getEmail(), s.getLangCode())));

        log.info( "companySubscriptions: {}", join( result, NotificationEntry::getAddress, ",") );
    }

    private List<CompanySubscription> filterByPlatformAndProduct(Set<CompanySubscription> companySubscriptions, Long platformId, Long productId) {
        return companySubscriptions.stream()
                .filter(companySubscription -> (companySubscription.getPlatformId() == null || Objects.equals(platformId, companySubscription.getPlatformId()))
                                            && (companySubscription.getProductId() == null || Objects.equals(productId, companySubscription.getProductId())))
                .collect( Collectors.toList());
    }

    private void appendProductSubscriptions( Long devUnitId, Set<NotificationEntry> result ) {
        safeGetByDevUnit(devUnitId)
                .forEach(s -> result.add(NotificationEntry.email(s.getEmail(), s.getLangCode())));
    }

    private void appendNotifiers(Long caseId, Set<NotificationEntry> result) {
        CaseObjectMetaNotifiers caseMetaNotifiers = caseObjectMetaNotifiersDAO.get(caseId);
        jdbcManyRelationsHelper.fill(caseMetaNotifiers, "notifiers");
        Set<Person> notifiers = CollectionUtils.emptyIfNull(caseMetaNotifiers.getNotifiers());
        jdbcManyRelationsHelper.fill(notifiers, Person.Fields.CONTACT_ITEMS);
        for (Person notifier : notifiers) {
            ContactItem email = notifier.getContactInfo().findFirst(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC);
            if (email == null) continue;
            result.add(NotificationEntry.email(email.value(), CrmConstants.LocaleTags.RU));
        }
    }

    private void appendNotifiersByFavoriteIssues(Long caseId, Set<NotificationEntry> result) {
        List<Long> personIdsByFavoriteIssueId = personFavoriteIssuesDAO.getPersonIdsByIssueId(caseId);

        if (CollectionUtils.isEmpty(personIdsByFavoriteIssueId)) {
            return;
        }

        result.addAll(stream(personIdsByFavoriteIssueId)
            .map(Person::new)
            .map(person -> {
                jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
                return person;
            })
            .map(Person::getContactInfo)
            .map(contactInfo -> contactInfo.findFirst(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC))
            .filter(Objects::nonNull)
            .map(email -> NotificationEntry.email(email.value(), CrmConstants.LocaleTags.RU))
            .collect(Collectors.toList())
        );
    }

    private static final Logger log = LoggerFactory.getLogger( CaseSubscriptionServiceImpl.class );
}
