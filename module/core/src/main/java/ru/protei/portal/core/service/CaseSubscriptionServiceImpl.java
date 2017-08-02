package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.ent.CompanySubscription;
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
    public Set<NotificationEntry> subscribers(CaseObjectEvent event) {
        return getByCompanyRule(event.getCaseObject().getInitiatorCompanyId());
    }

    @Override
    public Set<NotificationEntry> subscribers(CaseCommentEvent event) {
        return getByCompanyRule(event.getCaseObject().getInitiatorCompanyId());
    }


    private Set<NotificationEntry> getByCompanyRule (Long targetCompany) {
        Set<NotificationEntry> result = new HashSet<>();
        appendCompanySubscriptions(targetCompany, result);
        companyGroupHomeDAO.getAll().forEach( hc -> appendCompanySubscriptions(hc.getCompanyId(), result));
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
