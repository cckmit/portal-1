package ru.protei.portal.ui.education.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.ent.EducationWallet;
import ru.protei.portal.core.service.EducationService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.EducationController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service("EducationController")
public class EducationControllerImpl implements EducationController {

    @Override
    public List<EducationWallet> getAllWallets() throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(educationService.getAllWallets(token));
    }

    @Override
    public List<EducationEntry> getCurrentEntries() throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(educationService.getCurrentEntries(token));
    }

    @Override
    public EducationEntry requestNewEntry(EducationEntry entry, List<Long> workerIds) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(educationService.requestNewEntry(token, entry, workerIds));
    }

    @Override
    public EducationEntryAttendance requestNewAttendance(Long educationEntryId) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(educationService.requestNewAttendance(token, educationEntryId, token.getPersonId()));
    }

    @Override
    public List<EducationEntry> adminGetEntries(boolean showOnlyNotApproved, boolean showOutdated) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(educationService.adminGetEntries(token, showOnlyNotApproved, showOutdated));
    }

    @Override
    public EducationEntry adminModifyEntry(EducationEntry entry) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(educationService.adminModifyEntry(token, entry));
    }

    @Override
    public EducationEntry adminDeleteEntry(Long entryId) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(educationService.adminDeleteEntry(token, entryId));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    EducationService educationService;
}
