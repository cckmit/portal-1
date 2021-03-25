package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

import java.io.Serializable;
import java.util.List;

public interface HasFilterQueryIds extends FilterQuery, Serializable {
    List<Long> getAllCompanyIds();
    List<Long> getAllPersonIds();
    List<Long> getAllProductIds();
    List<Long> getAllDirectionIds();
    List<Long> getAllTagIds();
    List<Long> getAllRegionIds();
    Long getPlanId();
}
