package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.winter.jdbc.JdbcSort;

/**
 * Created by michael on 27.09.16.
 */
public class ProductServiceImpl implements ProductService {

    @Autowired
    DevUnitDAO devUnitDAO;

    @Override
    public HttpListResult<DevUnit> list(@RequestParam(name = "q", required = false) String param,
                                        @RequestParam(name = "sortBy", required = false) En_SortField sortField,
                                        @RequestParam(name = "sortDir", required = false) String sortDir) {
        param = HelperFunc.makeLikeArg(param, true);

        JdbcSort sort = new JdbcSort(En_SortDir.toWinter(sortDir), (sortField == null ? En_SortField.prod_name : sortField).getFieldName());

        return new HttpListResult<DevUnit>(devUnitDAO.getProductsByCondition(param, sort), false);
    }
}
