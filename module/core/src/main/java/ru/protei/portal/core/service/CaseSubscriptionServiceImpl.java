package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.*;

/**
 * Created by michael on 26.05.17.
 */
public class CaseSubscriptionServiceImpl implements CaseSubscriptionService {

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    CompanyGroupHomeDAO companyGroupHomeDAO;


    @Override
    public Set<NotificationEntry> subscribers(AssembledCaseEvent event) {
        return getByCase(event.getCaseObject());
    }

    private Set<NotificationEntry> getByCase (CaseObject caseObject){
        Set<NotificationEntry> result = new HashSet<>();
        appendCompanySubscriptions(caseObject.getInitiatorCompanyId(), result);
        if(caseObject.getNotifiers() != null){
            for(Person notifier: caseObject.getNotifiers()){
                ContactItem email = notifier.getContactInfo().findFirst(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC);
                if(email == null)
                    continue;
                result.add(NotificationEntry.email(email.value(), "ru"));
            }
        }
        //HomeCompany persons don't need to get notifications
//        companyGroupHomeDAO.getAll().forEach( hc -> appendCompanySubscriptions(hc.getCompanyId(), result));
        return result;
    }

    private List<CompanySubscription> safeGetByCompany( Long companyId) {
        return companyId == null ? Collections.emptyList() : companySubscriptionDAO.listByCompanyId(companyId);
    }

    private void appendCompanySubscriptions(Long companyId, Set<NotificationEntry> result) {
        safeGetByCompany(companyId)
                .forEach(s -> result.add(NotificationEntry.email(s.getEmail(), s.getLangCode())));
    }
}
