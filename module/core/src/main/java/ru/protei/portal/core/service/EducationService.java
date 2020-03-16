package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.ent.EducationWallet;

import java.util.List;

public interface EducationService {

    @Privileged(En_Privilege.EDUCATION_VIEW)
    Result<List<EducationWallet>> getAllWallets(AuthToken token);

    @Privileged(En_Privilege.EDUCATION_VIEW)
    Result<List<EducationEntry>> getCurrentEntries(AuthToken token);

    @Privileged(En_Privilege.EDUCATION_EDIT)
    Result<EducationEntry> requestNewEntry(AuthToken token, EducationEntry entry, List<Long> workerIds);

    @Privileged(En_Privilege.EDUCATION_VIEW)
    Result<EducationEntryAttendance> requestNewAttendance(AuthToken token, EducationEntryAttendance attendance);
}
