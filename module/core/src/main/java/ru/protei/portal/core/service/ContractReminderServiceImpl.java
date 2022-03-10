package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.ContractDateOneDayRemainingEvent;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.dao.ContractDateDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.api.struct.Result.ok;

public class ContractReminderServiceImpl implements ContractReminderService {
    private static final Logger log = LoggerFactory.getLogger(ContractReminderServiceImpl.class);

    @Autowired
    ContractDAO contractDAO;
    @Autowired
    ContractDateDAO contractDateDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    EventPublisherService publisherService;

    @Override
    public Result<Integer> notifyAboutDates() {
        log.info("notifyAboutDates(): start");

        LocalDateTime tomorrowStart = makeTomorrowWithTime(0, 0, 0);
        LocalDateTime tomorrowEnd = makeTomorrowWithTime(23, 59, 59);

        log.info("notifyAboutDates(): start for tomorrow from {} to {}", tomorrowStart, tomorrowEnd);

        List<ContractDate> contractDates = contractDateDAO.getNotifyBetweenDates(convertLocalDateTimeToDate(tomorrowStart), convertLocalDateTimeToDate(tomorrowEnd));
        if (CollectionUtils.isEmpty(contractDates)) {
            log.info("notifyAboutDates(): contractDates is empty for tomorrow");
            return ok(0);
        }

        int notificationSentAmount = 0;

        for (ContractDate contractDate : contractDates) {
            Contract contract = contractDAO.get(contractDate.getContractId());
            if (contract == null) {
                continue;
            }
            log.info("notifyAboutDates(): notification for contract={}", contract.getId());
            Set<Long> personIdList = makePersonIdListForNotification(contract);
            if (CollectionUtils.isEmpty(personIdList)) {
                log.info("notifyAboutDates(): notification for contract={}: no persons to be notified", contract.getId());
                continue;
            }
            Set<NotificationEntry> notificationEntries = getNotificationEntries(personIdList);
            if (CollectionUtils.isEmpty(notificationEntries)) {
                log.info("notifyAboutDates(): notification for contract={}: no entries to be notified", contract.getId());
                continue;
            }
            log.info("notifyAboutDates(): notification for contract={}: entries to be notified: {}", contract.getId(), notificationEntries);
            publisherService.publishEvent(new ContractDateOneDayRemainingEvent(this, contract, contractDate, notificationEntries));
            notificationSentAmount++;
        }

        log.info("notifyAboutDates(): done {} notification(s)", notificationSentAmount);
        return ok(notificationSentAmount);
    }

    private LocalDateTime makeTomorrowWithTime(int hour, int min, int sec) {
        LocalDateTime tomorrow = LocalDateTime.now();
        tomorrow = tomorrow.plusDays(1);
        tomorrow = tomorrow.withHour(hour);
        tomorrow = tomorrow.withMinute(min);
        tomorrow = tomorrow.withSecond(sec);
        tomorrow = tomorrow.withNano(0);
        return tomorrow;
    }

    private Date convertLocalDateTimeToDate(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Set<NotificationEntry> getNotificationEntries(Set<Long> personIdList) {
        Set<NotificationEntry> notificationEntries = new HashSet<>();
        List<Person> personList = personDAO.partialGetListByKeys(personIdList, "id", "locale");
        jdbcManyRelationsHelper.fill(personList, Person.Fields.CONTACT_ITEMS);
        for (Person person : personList) {
            ContactInfo contactInfo = person.getContactInfo();
            if (contactInfo == null) {
                continue;
            }
            String email = new PlainContactInfoFacade(contactInfo).getEmail();
            String locale = person.getLocale() == null ? CrmConstants.DEFAULT_LOCALE : person.getLocale();
            if (StringUtils.isBlank(email)) {
                continue;
            }
            notificationEntries.add(NotificationEntry.email(email, locale));
        }
        return notificationEntries;
    }

    private Set<Long> makePersonIdListForNotification(Contract contract) {
        Set<Long> personIdList = new HashSet<>();
        if (contract.getProjectManagerId() != null) {
            personIdList.add(contract.getProjectManagerId());
        }
        if (contract.getCuratorId() != null) {
            personIdList.add(contract.getCuratorId());
        }
        return personIdList;
    }
}
