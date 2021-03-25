package ru.protei.portal.core.model.query;

import java.util.List;

public interface HasFilterEntityIds {
    List<Long> getAllCompanyIds();
    List<Long> getAllPersonIds();
    List<Long> getAllProductIds();
    List<Long> getAllDirectionIds();
    List<Long> getAllTagIds();
    List<Long> getAllRegionIds();
    Long getPlanId();
}
