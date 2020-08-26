package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.BaseQuery;

import java.io.Serializable;

public interface ReportDto extends Serializable {

    Report getReport();

    BaseQuery getQuery();
}
