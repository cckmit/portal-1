package ru.protei.portal.core.service.dict;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.ent.Company;

/**
 * Created by michael on 27.09.16.
 */
@RestController
@RequestMapping(path = "/api/gate/company")
public interface CompanyService {

    @RequestMapping(path = "/list")
    public HttpListResult<Company> list(@RequestParam(name = "q", defaultValue = "") String param);

}
