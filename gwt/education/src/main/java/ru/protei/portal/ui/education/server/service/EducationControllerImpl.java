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
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
    public SearchResult<EducationEntry> adminGetEntries(int offset, int limit, boolean showOnlyNotApproved, boolean showOutdated) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(educationService.adminGetEntries(token, offset, limit, showOnlyNotApproved, showOutdated));
    }

    @Override
    public EducationEntry adminSaveEntryAndAttendance(EducationEntry entry, Map<Long, Boolean> worker2approve) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(educationService.adminSaveEntryAndAttendance(token, entry, worker2approve));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    EducationService educationService;
}
