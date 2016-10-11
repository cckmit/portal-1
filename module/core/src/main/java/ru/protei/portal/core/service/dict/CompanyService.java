package ru.protei.portal.core.service.dict;

import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanyGroupItem;

/**
 * Created by michael on 27.09.16.
 */
@RestController
@RequestMapping(path = "/api/gate/company")
public interface CompanyService {

    @RequestMapping(path = "/list")
    HttpListResult<Company> list(@RequestParam(name = "q", required = false) String param,
                                 @RequestParam(name = "group", required = false) Long groupId,
                                 @RequestParam(name = "sortBy", required = false) En_SortField sortField,
                                 @RequestParam(name = "sortDir", required = false) String sortDir);

    @GetMapping(path = "/profile/{id:[0-9]+}.json")
    Company getProfile(@PathVariable("id") Long id);


/*    @RequestMapping(path = "/list-view")
    HttpListResult<Company> listView (@RequestParam(name = "q", required = false) String param,
                                      @RequestParam(name = "sortBy", required = false) En_SortField sortField,
                                      @RequestParam(name = "sortDir", required = false) String sortDir);*/


    @RequestMapping(path = "/group/list")
    HttpListResult<CompanyGroup> groupList (@RequestParam(name = "q", required = false) String param,
                                            @RequestParam(name = "sortBy", required = false) En_SortField sortField,
                                            @RequestParam(name = "sortDir", required = false) String sortDir);

    @RequestMapping(path = "/group/create")
    CoreResponse<CompanyGroup> createGroup (@RequestParam(name = "name") String name,
                                            @RequestParam(name = "info") String info);

    @RequestMapping(path = "/add-to-group")
    CoreResponse<CompanyGroupItem> addCompanyToGroup (@RequestParam(name="group") Long groupId,
                                                      @RequestParam(name="company") Long companyId);

    @RequestMapping(path = "/del-from-group")
    CoreResponse<CompanyGroupItem> delCompanyFromGroup (@RequestParam(name="group") Long groupId,
                                                      @RequestParam(name="company") Long companyId);

}
