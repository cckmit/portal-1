package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.util.CrmConstants.State.PAUSED;

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
     * Описание проекта
     */
    private String description;

    /**
     * Состояние проекта
     */
    private Long stateId;

    private Long pauseDate;

    /**
     * Дата создания
     */
    private Date created;

    /**
     * Компания
     */
    private Long customerId;

    /**
     * Тип заказчика
     */
    private En_CustomerType customerType;

    private EntityOption region;

    private Set<EntityOption> productDirection;

    private EntityOption manager;

    private EntityOption contragent;

    private Set<ProductShortView> products;

    private Date technicalSupportValidity;

    private Date workCompletionDate;

    private Date purchaseDate;

    private List<PersonProjectMemberView> team;

    private List<ProjectSla> projectSlas;

    private List<Plan> projectPlans;

    private List<Company> subcontractors;

    public ProjectInfo() {
    }

    public ProjectInfo(Long id, String name, Date created, En_CustomerType customerType, EntityOption region,
                       Set<EntityOption> productDirection, EntityOption manager, EntityOption contragent, Set<ProductShortView> products, Date technicalSupportValidity) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.customerType = customerType;
        this.region = region;
        this.productDirection = productDirection;
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

    public String getDescription() {
        return description;
    }

    public Long getStateId() {
        return stateId;
    }

    public Date getCreated() {
        return created;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public En_CustomerType getCustomerType() {
        return customerType;
    }

    public List<PersonProjectMemberView> getTeam() {
        return team;
    }

    public EntityOption getRegion() {
        return region;
    }

    public Set<EntityOption> getProductDirection() {
        return productDirection;
    }

    public EntityOption getManager() {
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

    public Date getWorkCompletionDate() {
        return workCompletionDate;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public Long getPauseDate() {
        return pauseDate;
    }

    public List<ProjectSla> getProjectSlas() {
        return projectSlas;
    }

    public List<Company> getSubcontractors() {
        return subcontractors;
    }

    public List<Plan> getProjectPlans() {
        return projectPlans;
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
                project.getManagerId() == null || project.getManagerName() == null ? null : new EntityOption(project.getManagerName(), project.getManagerId()),
                project.getCustomer() == null ? null : new EntityOption(project.getCustomer().getCname(), project.getCustomer().getId()),
                project.getProducts() == null ? null : project.getProducts().stream().map(ProductShortView::fromProduct).collect(Collectors.toSet()),
                project.getTechnicalSupportValidity());
    }

    public static Project fromProjectInfo(ProjectInfo info) {
        if (info == null)
            return null;

        Project project = new Project();
        project.setCreated(new Date());

        project.setName(info.getName());
        project.setCustomer(new Company(info.getCustomerId()));
        project.setCustomerType(info.getCustomerType());

        if (info.getProductDirection() != null) {
            Set<DevUnit> devUnitProductDirections = new HashSet<DevUnit>();
            for (EntityOption option: info.getProductDirection()) {
                devUnitProductDirections.add(DevUnit.fromEntityOption(option));
            }

            project.setProductDirections(devUnitProductDirections);
        }

        project.setTeam(info.getTeam());

        project.setStateId(info.getStateId() == null ? 0 : info.getStateId());
        project.setDescription(info.getDescription());
        project.setStateId(info.getStateId());

        if (info.getStateId().equals(PAUSED)) {
            project.setPauseDate(info.getPauseDate());
        }

        project.setRegion(info.getRegion());

        if (info.getProducts() != null) {
            Set<DevUnit> devUnitProducts = new HashSet<DevUnit>();
            for (ProductShortView view: info.getProducts()) {
                devUnitProducts.add(DevUnit.fromProductShortView(view));
            }

            project.setProducts(devUnitProducts);
        }

        project.setTechnicalSupportValidity(info.getTechnicalSupportValidity());
        project.setWorkCompletionDate(info.getWorkCompletionDate());
        project.setPurchaseDate(info.getPurchaseDate());

        project.setSubcontractors(info.getSubcontractors());
        project.setProjectPlans(info.getProjectPlans());
        project.setProjectSlas(info.getProjectSlas());

        return project;
    }
}
