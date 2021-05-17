package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

import java.io.Serializable;
import java.util.List;

@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY
)
public interface HasFilterQueryIds extends FilterQuery, Serializable {
    List<Long> getAllCompanyIds();
    List<Long> getAllPersonIds();
    List<Long> getAllProductIds();
    List<Long> getAllDirectionIds();
    List<Long> getAllTagIds();
    List<Long> getAllRegionIds();
    List<Long> getAllPlatformIds();
    Long getPlanId();
}
