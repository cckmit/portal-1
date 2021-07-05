package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

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
     * Дата создания
     */
    private Date created;

    /**
     * Тип заказчика
     */
    private En_CustomerType customerType;

    private EntityOption region;

    private Set<EntityOption> productDirection;

    private String managerCompany;

    private PersonShortView manager;

    private EntityOption contragent;

    private Set<ProductShortView> products;

    private Date technicalSupportValidity;

    public ProjectInfo() {
    }

    public ProjectInfo(Long id, String name, Date created, En_CustomerType customerType, EntityOption region,
                       Set<EntityOption> productDirection, String managerCompany, PersonShortView manager,
                       EntityOption contragent, Set<ProductShortView> products, Date technicalSupportValidity) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.customerType = customerType;
        this.region = region;
        this.productDirection = productDirection;
        this.managerCompany = managerCompany;
        this.manager = manager;
        this.contragent = contragent;
        this.products = products;
        this.technicalSupportValidity = technicalSupportValidity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreated() {
        return created;
    }

    public En_CustomerType getCustomerType() {
        return customerType;
    }

    public EntityOption getRegion() {
        return region;
    }

    public Set<EntityOption> getProductDirection() {
        return productDirection;
    }

    public String getManagerCompany() {
        return managerCompany;
    }

    public PersonShortView getManager() {
        return manager;
    }

    public EntityOption getContragent() {
        return contragent;
    }

    public Set<ProductShortView> getProducts() {
        return products;
    }

    public Date getTechnicalSupportValidity() {
        return technicalSupportValidity;
    }

    public void setTechnicalSupportValidity(Date technicalSupportValidity) {
        this.technicalSupportValidity = technicalSupportValidity;
    }

    public static ProjectInfo fromProject(Project project) {
        if (project == null)
            return null;

        return new ProjectInfo(
                project.getId(),
                project.getName(),
                project.getCreated(),
                project.getCustomerType(),
                CollectionUtils.isEmpty(project.getLocations()) ? null : EntityOption.fromLocation(project.getLocations().get(0).getLocation()),
                new HashSet<>(emptyIfNull(project.getProductDirectionEntityOptionList())),
                project.getManagerCompanyName(),
                project.getManagerId() == null || project.getManagerName() == null ? null : new PersonShortView(project.getManagerName(), project.getManagerFullName(), project.getManagerId()),
                project.getCustomer() == null ? null : new EntityOption(project.getCustomer().getCname(), project.getCustomer().getId()),
                project.getProducts() == null ? null : project.getProducts().stream().map(ProductShortView::fromProduct).collect(Collectors.toSet()),
                project.getTechnicalSupportValidity());
    }
}
