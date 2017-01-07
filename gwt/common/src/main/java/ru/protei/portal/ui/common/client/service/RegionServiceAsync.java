package ru.protei.portal.ui.common.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.query.RegionQuery;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.List;

/**
 * Асинхронный сервис управления продуктами
 */
public interface RegionServiceAsync {
    void getRegionList( RegionQuery query, AsyncCallback< List< RegionInfo > > async );
}
