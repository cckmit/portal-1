package ru.protei.portal.core.service.dict;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.view.ProductView;

/**
 * Created by michael on 27.09.16.
 */
@RestController
@RequestMapping(path = "/api/gate/product")
public interface ProductService {

    @RequestMapping(path = "/list")
    public HttpListResult<ProductView> list(@RequestParam(name = "q", defaultValue = "") String param);

}
