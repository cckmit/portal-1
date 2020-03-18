package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.ent.EducationWallet;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/EducationController")
public interface EducationController extends RemoteService {

    List<EducationWallet> getAllWallets() throws RequestFailedException;

    List<EducationEntry> getCurrentEntries() throws RequestFailedException;

    EducationEntry requestNewEntry(EducationEntry entry, List<Long> workerIds) throws RequestFailedException;

    EducationEntryAttendance requestNewAttendance(Long educationEntryId) throws RequestFailedException;

    List<EducationEntry> adminGetEntries(boolean showOnlyNotApproved, boolean showOutdated) throws RequestFailedException;

    EducationEntry adminModifyEntry(EducationEntry entry) throws RequestFailedException;

    EducationEntry adminDeleteEntry(Long entryId) throws RequestFailedException;
}
