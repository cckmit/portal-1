package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.ent.EducationWallet;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("springGwtServices/EducationController")
public interface EducationController extends RemoteService {

    List<EducationWallet> getAllWallets() throws RequestFailedException;

    List<EducationEntry> getCurrentEntries() throws RequestFailedException;

    EducationEntry requestNewEntry(EducationEntry entry, List<Long> workerIds) throws RequestFailedException;

    EducationEntryAttendance requestNewAttendance(Long educationEntryId) throws RequestFailedException;

    SearchResult<EducationEntry> adminGetEntries(int offset, int limit, boolean showOnlyNotApproved, boolean showOutdated) throws RequestFailedException;

    EducationEntry adminSaveEntryAndAttendance(EducationEntry entry, Map<Long, Boolean> worker2approve) throws RequestFailedException;
}
