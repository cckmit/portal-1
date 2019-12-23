package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;

import java.io.Serializable;

public class ProjectInfo implements Serializable {

    /**
     * Идентификатор записи о проекте
     */
    private Long id;

    /**
     * Название проекта
     */
    private String name;

    /**
     * Тип заказчика
     */
    private En_CustomerType customerType;

    private EntityOption region;

    private EntityOption productDirection;

    private EntityOption manager;

    private EntityOption contragent;

    public ProjectInfo() {
    }

    public ProjectInfo(Long id, String name, En_CustomerType customerType, EntityOption region, EntityOption productDirection, EntityOption manager, EntityOption contragent) {
        this.id = id;
        this.name = name;
        this.customerType = customerType;
        this.region = region;
        this.productDirection = productDirection;
        this.manager = manager;
        this.contragent = contragent;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public En_CustomerType getCustomerType() {
        return customerType;
    }

    public EntityOption getRegion() {
        return region;
    }

    public EntityOption getProductDirection() {
        return productDirection;
    }

    public EntityOption getManager() {
        return manager;
    }

    public EntityOption getContragent() {
        return contragent;
    }

    public static ProjectInfo fromCaseObject(CaseObject project) {
        if (project == null)
            return null;

        return new ProjectInfo(
                project.getId(),
                project.getName(),
                En_CustomerType.find(project.getLocal()),
                CollectionUtils.isEmpty(project.getLocations()) ? null : EntityOption.fromLocation(project.getLocations().get(0).getLocation()),
                project.getProduct() == null ? null : new EntityOption(project.getProduct().getName(), project.getProduct().getId()),
                project.getManager() == null ? null : new EntityOption(project.getManager().getDisplayShortName(), project.getManagerId()),
                project.getInitiatorCompany() == null ? null : new EntityOption(project.getInitiatorCompany().getCname(), project.getInitiatorCompanyId()));
    }

    public static ProjectInfo fromProject(Project project) {
        if (project == null)
            return null;

        return new ProjectInfo(
                project.getId(),
                project.getName(),
                project.getCustomerType(),
                project.getRegion(),
                project.getProductDirection(),
                project.getManager(),
                project.getContragent());
    }
}
