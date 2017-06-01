package ru.protei.portal.ui.common.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
import java.util.Map;

/**
 * Асинхронный сервис управления продуктами
 */
public interface RegionServiceAsync {
    void getRegionList( ProjectQuery query, AsyncCallback< List< RegionInfo > > async );

    void getDistrictList( AsyncCallback<List<DistrictInfo>> callback );

    void getProjectsByRegions( ProjectQuery query, AsyncCallback<Map<String,List<ProjectInfo>>> callback );

    void getProject( Long id, AsyncCallback<ProjectInfo> callback );

    void saveProject( ProjectInfo project, AsyncCallback<Void> callback );

    void getRegionList( AsyncCallback<List<EntityOption>> callback );

    void createNewProject( AsyncCallback<Long> callback );
}