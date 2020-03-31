package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.ent.EducationWallet;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Map;

public interface EducationControllerAsync {

    void getAllWallets(AsyncCallback<List<EducationWallet>> async);

    void getCurrentEntries(AsyncCallback<List<EducationEntry>> async);

    void requestNewEntry(EducationEntry entry, List<Long> workerIds, AsyncCallback<EducationEntry> async);

    void requestNewAttendance(Long educationEntryId, AsyncCallback<EducationEntryAttendance> async);

    void adminGetEntries(int offset, int limit, boolean showOnlyNotApproved, boolean showOutdated, AsyncCallback<SearchResult<EducationEntry>> async);

    void adminSaveEntryAndAttendance(EducationEntry entry, Map<Long, Boolean> worker2approve, AsyncCallback<EducationEntry> async);
}
