package ru.protei.portal.mapper;

import ru.protei.portal.core.model.api.ApiProject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Date;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.util.CrmConstants.State.PAUSED;
import static ru.protei.portal.core.model.util.CrmConstants.State.UNKNOWN;

public class ApiProjectToProjectMapper {

    public static Project toProject(ApiProject api) {
        if (api == null) {
            return null;
        }

        Project project = new Project();
        project.setCreated(new Date());
        project.setName(api.getName());
        project.setDescription(api.getDescription());
        project.setStateId(api.getStateId() == null ? UNKNOWN : api.getStateId());
        project.setPauseDate(project.getStateId().equals(PAUSED) ? api.getPauseDate() : null);
        project.setRegion(new EntityOption(api.getRegionId()));
        project.setCustomer(new Company(api.getCompanyId()));
        project.setCustomerType(En_CustomerType.find(api.getCustomerTypeId()));
        project.setTeam(api.getTeam());
        project.setTechnicalSupportValidity(api.getTechnicalSupportValidity());
        project.setWorkCompletionDate(api.getWorkCompletionDate());
        project.setPurchaseDate(api.getPurchaseDate());
        project.setProjectSlas(isEmpty(api.getProjectSlas()) ? null : api.getProjectSlas());

        project.setProductDirections(isEmpty(api.getDirectionsIds()) ? null :
                api.getDirectionsIds().stream().map(DevUnit::new).collect(toSet()));

        project.setProducts(isEmpty(api.getProductsIds()) ? null :
                api.getProductsIds().stream().map(DevUnit::new).collect(toSet()));

        project.setSubcontractors(isEmpty(api.getSubcontractorsIds()) ? null :
                api.getSubcontractorsIds().stream().map(Company::new).collect(toList()));

        project.setProjectPlans(isEmpty(api.getProjectPlansIds()) ? null :
                api.getProjectPlansIds().stream().map(Plan::new).collect(toList()));

        return project;
    }
}
