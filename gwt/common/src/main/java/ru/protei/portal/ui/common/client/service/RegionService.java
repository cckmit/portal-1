package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.query.RegionQuery;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления продуктами
 */
@RemoteServiceRelativePath( "springGwtServices/RegionService" )
public interface RegionService extends RemoteService {

    List<RegionInfo> getRegionList( RegionQuery query ) throws RequestFailedException;

//    DevUnit getProduct( Long productId ) throws RequestFailedException;

//    Boolean saveProduct( DevUnit product ) throws RequestFailedException;

//    boolean isNameUnique( String name, Long exceptId ) throws RequestFailedException;

//    List<ProductShortView> getProductViewList( ProductQuery query ) throws RequestFailedException;
}
