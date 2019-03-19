package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ContractDateDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.ContactInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContractReminderServiceImpl implements ContractReminderService {

    @Override
    public CoreResponse<Boolean> notifyAboutDates() {
        Calendar now = Calendar.getInstance();
        Calendar tomorrowStart = makeTomorrowWithTime(now, 0, 0, 0);
        Calendar tomorrowEnd = makeTomorrowWithTime(now, 23, 59, 59);

        List<ContractDate> contractDates = contractDateDAO.getNotifyBetweenDates(tomorrowStart.getTime(), tomorrowEnd.getTime());
        if (CollectionUtils.isEmpty(contractDates)) {
            return new CoreResponse<Boolean>().success(true);
        }

        for (ContractDate contractDate : contractDates) {
            CaseObject caseObject = caseObjectDAO.partialGet(contractDate.getId(), "MANAGER", "INITIATOR");
            List<ContactInfo> contactInfoList = getContacts(caseObject);

            // to be continued...
            // send notify mails to contract's contacts

        }

        return null;
    }

    private Calendar makeTomorrowWithTime(Calendar now, int hour, int min, int sec) {
        Calendar tomorrow = (Calendar) now.clone();
        tomorrow.add(Calendar.DATE, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, hour);
        tomorrow.set(Calendar.MINUTE, min);
        tomorrow.set(Calendar.SECOND, sec);
        return tomorrow;
    }

    private List<ContactInfo> getContacts(CaseObject caseObject) {
        List<ContactInfo> contactInfoList = new ArrayList<>();
        if (caseObject.getManagerId() != null) {
            ContactInfo contactInfo = personDAO.partialGet(caseObject.getManagerId(), "contactInfo").getContactInfo();
            contactInfoList.add(contactInfo);
        }
        if (caseObject.getInitiatorId() != null) {
            ContactInfo contactInfo = personDAO.partialGet(caseObject.getInitiatorId(), "contactInfo").getContactInfo();
            contactInfoList.add(contactInfo);
        }
        return contactInfoList;
    }

    @Autowired
    ContractDateDAO contractDateDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    PersonDAO personDAO;

}
