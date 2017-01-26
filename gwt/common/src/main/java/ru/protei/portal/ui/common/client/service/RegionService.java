package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Map;

/**
 * Сервис управления продуктами
 */
@RemoteServiceRelativePath( "springGwtServices/RegionService" )
public interface RegionService extends RemoteService {

    List<RegionInfo> getRegionList( ProjectQuery query ) throws RequestFailedException;

    List<DistrictInfo> getDistrictList() throws RequestFailedException;

    Map<String, List<ProjectInfo>> getProjectsByRegions( ProjectQuery query ) throws RequestFailedException;

//    DevUnit getProduct( Long productId ) throws RequestFailedException;

//    Boolean saveProduct( DevUnit product ) throws RequestFailedException;

//    boolean isNameUnique( String name, Long exceptId ) throws RequestFailedException;

//    List<ProductShortView> getProductViewList( ProductQuery query ) throws RequestFailedException;
}
