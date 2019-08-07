package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления продуктами
 */
@RemoteServiceRelativePath("springGwtServices/ProductController")
public interface ProductController extends RemoteService {

    SearchResult<DevUnit> getProductList (ProductQuery query) throws RequestFailedException;

    DevUnit getProduct(Long productId) throws RequestFailedException;

    DevUnit saveProduct(DevUnit product) throws RequestFailedException;

    boolean isNameUnique(String name, Long exceptId) throws RequestFailedException;

    /**
     * Получение списка сокращенного представления продукта
     * @param query запрос
     * @return
     */
    List<ProductShortView> getProductViewList(ProductQuery query) throws RequestFailedException;

    /**
     * Получение списка продуктовых направлений
     *
     * @param query
     */
    List<ProductDirectionInfo> getProductDirectionList(ProductDirectionQuery query) throws RequestFailedException;
}
