package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.ent.EducationWallet;

import java.util.List;

public interface EducationControllerAsync {

    void getAllWallets(AsyncCallback<List<EducationWallet>> async);

    void getCurrentEntries(AsyncCallback<List<EducationEntry>> async);

    void requestNewEntry(EducationEntry entry, List<Long> workerIds, AsyncCallback<EducationEntry> async);

    void requestNewAttendance(Long educationEntryId, AsyncCallback<EducationEntryAttendance> async);

    void adminGetEntries(boolean showOnlyNotApproved, boolean showOutdated, AsyncCallback<List<EducationEntry>> async);

    void adminModifyEntry(EducationEntry entry, AsyncCallback<EducationEntry> async);

    void adminDeleteEntry(Long entryId, AsyncCallback<EducationEntry> async);
}
