package ru.protei.portal.core.client.enterprise1c.api;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.enterprise1c.dto.WorkPersonInfo1C;
import ru.protei.portal.core.model.enterprise1c.query.WorkQuery1C;

public interface Api1CWork {
    Result<WorkPersonInfo1C> getProteiWorkPersonInfo(WorkQuery1C query);

    Result<WorkPersonInfo1C> getProteiStWorkPersonInfo(WorkQuery1C query);

    Result<String> getEmployeeRestVacationDays(String workerExtId, String companyName);
}
