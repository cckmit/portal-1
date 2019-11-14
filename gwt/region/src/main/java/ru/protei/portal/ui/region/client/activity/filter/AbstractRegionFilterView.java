package ru.protei.portal.ui.region.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;

import java.util.Set;

/**
 * Абстракция вида фильтра регионов
 */
public interface AbstractRegionFilterView extends IsWidget {
    void setActivity( AbstractRegionFilterActivity activity );

    HasValue< String > searchPattern();

    HasValue< Set<DistrictInfo>> districts();

    void resetFilter();
}