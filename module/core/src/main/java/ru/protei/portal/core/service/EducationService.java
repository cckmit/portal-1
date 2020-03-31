package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.ent.EducationWallet;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Map;

public interface EducationService {

    @Privileged(En_Privilege.EDUCATION_VIEW)
    Result<List<EducationWallet>> getAllWallets(AuthToken token);

    @Privileged(En_Privilege.EDUCATION_VIEW)
    Result<List<EducationEntry>> getCurrentEntries(AuthToken token);

    @Privileged(requireAny = { En_Privilege.EDUCATION_EDIT, En_Privilege.EDUCATION_CREATE })
    Result<EducationEntry> requestNewEntry(AuthToken token, EducationEntry entry, List<Long> workerIds);

    @Privileged(En_Privilege.EDUCATION_VIEW)
    Result<EducationEntryAttendance> requestNewAttendance(AuthToken token, Long educationEntryId, Long personId);

    @Privileged(En_Privilege.EDUCATION_CREATE)
    Result<SearchResult<EducationEntry>> adminGetEntries(AuthToken token, int offset, int limit, boolean showOnlyNotApproved, boolean showOutdated);

    @Privileged(En_Privilege.EDUCATION_CREATE)
    Result<EducationEntry> adminSaveEntryAndAttendance(AuthToken token, EducationEntry entry, Map<Long, Boolean> worker2approve);
}
