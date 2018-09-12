package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.EmployeeRegistrationEvent;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dao.ProductSubscriptionDAO;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.ent.DevUnitSubscription;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by michael on 26.05.17.
 */
public class CaseSubscriptionServiceImpl implements CaseSubscriptionService {

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    ProductSubscriptionDAO productSubscriptionDAO;

    @Autowired
    CompanyGroupHomeDAO companyGroupHomeDAO;


    @Override
    public Set<NotificationEntry> subscribers(AssembledCaseEvent event) {
        return getByCase(event.getCaseObject());
    }

    @Override
    public Set<NotificationEntry> subscribers(EmployeeRegistrationEvent event) {
        return new HashSet<NotificationEntry>(){{add(new NotificationEntry("efremov@protei.ru", En_ContactItemType.EMAIL, "ru"));}};
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

    private List<CompanySubscription> safeGetByCompany( Long companyId) {
        return companyId == null ? Collections.emptyList() : companySubscriptionDAO.listByCompanyId(companyId);
    }

    private List<DevUnitSubscription> safeGetByDevUnit(Long devUnitId) {
        return devUnitId == null ? Collections.emptyList() : productSubscriptionDAO.listByDevUnitId(devUnitId);
    }

    private void appendCompanySubscriptions(Long companyId, Set<NotificationEntry> result) {
        safeGetByCompany(companyId)
                .forEach(s -> result.add(NotificationEntry.email(s.getEmail(), s.getLangCode())));
    }

    private void appendProductSubscriptions( Long devUnitId, Set<NotificationEntry> result ) {
        safeGetByDevUnit(devUnitId)
                .forEach(s -> result.add(NotificationEntry.email(s.getEmail(), s.getLangCode())));
    }
}
