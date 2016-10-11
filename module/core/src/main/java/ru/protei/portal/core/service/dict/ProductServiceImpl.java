package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.ProductDAO;
import ru.protei.portal.core.model.view.ProductView;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.winter.jdbc.JdbcSort;

/**
 * Created by michael on 27.09.16.
 */
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDAO productDAO;

    @Override
    public HttpListResult<ProductView> list(@RequestParam(name = "q", defaultValue = "") String param) {

        param = HelperFunc.makeLikeArg(param,true);

        JdbcSort sort = new JdbcSort(JdbcSort.Direction.ASC, "name");

        return new HttpListResult<>(productDAO.getListByCondition("name like ?", sort, param), false);
    }
}
