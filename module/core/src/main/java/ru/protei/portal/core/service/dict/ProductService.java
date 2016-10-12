package ru.protei.portal.core.service.dict;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DevUnit;

/**
 * Created by michael on 27.09.16.
 */
@RestController
@RequestMapping(path = "/api/gate/product")
public interface ProductService {

    @RequestMapping(path = "/list")
    HttpListResult<DevUnit> list(@RequestParam(name = "q", required = false) String param,
                                 @RequestParam(name = "state", required = false) En_DevUnitState state,
                                        @RequestParam(name = "sortBy", required = false) En_SortField sortField,
                                        @RequestParam(name = "sortDir", required = false) String sortDir);

}
