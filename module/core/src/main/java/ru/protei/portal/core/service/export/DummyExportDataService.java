package ru.protei.portal.core.service.export;

import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;

public class DummyExportDataService implements ExportDataService {
    @Override
    public En_ResultStatus exportCompany(Company company) {
        return En_ResultStatus.OK;
    }

    @Override
    public En_ResultStatus exportPerson(Person person) {
        return En_ResultStatus.OK;
    }

    @Override
    public En_ResultStatus exportProduct(DevUnit product) {
        return En_ResultStatus.OK;
    }
}
