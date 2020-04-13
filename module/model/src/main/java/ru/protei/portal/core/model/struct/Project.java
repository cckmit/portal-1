package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Информация о проекте в регионе
 */
public class Project extends AuditableObject {

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
     * Текущее состояние проекта
     */
    private Long stateId;

    /**
     * Тип заказчика
     */
    private En_CustomerType customerType;

    /**
     * Заказчик
     */
    private Company customer;

    /**
     * продуктовое направление
     */
    private EntityOption productDirection;

    /**
     * Дата создания
     */
    private Date created;

    /**
     * Создатель проекта
     */
    private Long creatorId;

    /**
     * Команда проекта
     */
    private List<PersonProjectMemberView> team;

    private EntityOption region;

    private List<CaseLink> links;

    private Set<ProductShortView> products;

    private Person creator;

    private List<EntityOption> contracts;

    private boolean deleted;

    private EntityOption manager;

    private EntityOption contragent;

    private String platformName;

    private Long platformId;

    private Date technicalSupportValidity;

    private List<ProjectSla> projectSlas;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public En_RegionState getState() {
        return stateId == null ? En_RegionState.UNKNOWN : En_RegionState.forId( stateId );
    }

    public void setState( En_RegionState state ) {
        this.stateId = state.getId();
    }

    public EntityOption getProductDirection() {
        return productDirection;
    }

    public void setProductDirection( EntityOption productDirection ) {
        this.productDirection = productDirection;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId( Long creatorId ) {
        this.creatorId = creatorId;
    }

    public EntityOption getRegion() {
        return region;
    }

    public void setRegion( EntityOption region ) {
        this.region = region;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public En_CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(En_CustomerType customerType) {
        this.customerType = customerType;
    }

    public Company getCustomer() {
        return customer;
    }

    public void setCustomer(Company customer) {
        this.customer = customer;
    }

    public Set<ProductShortView> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductShortView> products) {
        this.products = products;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public List<PersonProjectMemberView> getTeam() {
        return team;
    }

    public PersonProjectMemberView getLeader() {
        if (team == null) {
            return null;
        }
        return team.stream()
                .filter(member -> En_DevUnitPersonRoleType.HEAD_MANAGER.equals(member.getRole()))
                .findFirst()
                .orElse(null);
    }

    public void setTeam(List<PersonProjectMemberView> team) {
        this.team = team;
    }

    public void addTeamMember(PersonProjectMemberView person) {
        if (team == null) {
            team = new ArrayList<>();
        }
        team.add(person);
    }

    public List<CaseLink> getLinks() {
        return links;
    }

    public void addLink(CaseLink link) {
        if (links == null) {
            links = new ArrayList<>();
        }
        links.add(link);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public ProductShortView getSingleProduct() {
        return products == null ? null : products.stream().findAny().orElse(null);
    }

    public List<EntityOption> getContracts() {
        return contracts;
    }

    public void setContracts(List<EntityOption> contracts) {
        this.contracts = contracts;
    }

    public EntityOption getManager() {
        return manager;
    }

    public void setManager(EntityOption manager) {
        this.manager = manager;
    }

    public EntityOption getContragent() {
        return contragent;
    }

    public void setContragent(EntityOption contragent) {
        this.contragent = contragent;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Date getTechnicalSupportValidity() {
        return technicalSupportValidity;
    }

    public void setTechnicalSupportValidity(Date technicalSupportValidity) {
        this.technicalSupportValidity = technicalSupportValidity;
    }

    public List<ProjectSla> getProjectSlas() {
        return projectSlas;
    }

    public void setProjectSlas(List<ProjectSla> projectSlas) {
        this.projectSlas = projectSlas;
    }

    public static Project fromCaseObject(CaseObject project ) {
        if (project == null)
            return null;

        Project projectInfo = new Project();
        projectInfo.setId( project.getId() );
        projectInfo.setName( project.getName() );
        projectInfo.setCreator(project.getCreator());
        projectInfo.setDescription(project.getInfo());
        projectInfo.setState( En_RegionState.forId( project.getStateId() ) );
        projectInfo.setDeleted(project.isDeleted());
        if ( project.getProduct() != null ) {
            projectInfo.setProductDirection( new EntityOption(
                project.getProduct().getName(), project.getProduct().getId()
            ) );
        }

        projectInfo.setCustomerType(En_CustomerType.find(project.getLocal()));
        projectInfo.setCustomer(project.getInitiatorCompany());

        projectInfo.setTeam(new ArrayList<>());
        if (project.getMembers() != null) {
            List<En_DevUnitPersonRoleType> projectRoles = En_DevUnitPersonRoleType.getProjectRoles();
            project.getMembers().stream()
                    .filter(member -> projectRoles.contains(member.getRole()))
                    .map(member -> PersonProjectMemberView.fromFullNamePerson(member.getMember(), member.getRole()))
                    .forEach(projectInfo::addTeamMember);
        }

        projectInfo.setCreated( project.getCreated() );

        List<CaseLocation> locations = project.getLocations();
        if ( locations != null && !locations.isEmpty() ) {
            projectInfo.setRegion( EntityOption.fromLocation( locations.get( 0 ).getLocation() ) );
        }

        if (project.getProducts() != null) {
            projectInfo.setProducts( project.getProducts().stream()
                                        .map(ProductShortView::fromProduct)
                                        .collect(Collectors.toSet()) );
        }

        projectInfo.setContracts(project.getContracts());

        if (project.getManager() != null) {
            projectInfo.setManager(new EntityOption(project.getManager().getDisplayShortName(), project.getManagerId()));
        }

        if (project.getInitiatorCompany() != null) {
            projectInfo.setContragent(new EntityOption(project.getInitiatorCompany().getCname(), project.getInitiatorCompanyId()));
        }

        if (project.getPlatformId() != null){
            projectInfo.setPlatformId(project.getPlatformId());
            projectInfo.setPlatformName(project.getPlatformName());

        }

        projectInfo.setTechnicalSupportValidity(project.getTechnicalSupportValidity());

        projectInfo.setProjectSlas(project.getProjectSlas());

        return projectInfo;
    }

    @Override
    public String getAuditType() {
        return "Project";
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Project that = (Project) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", stateId=" + stateId +
                ", customerType=" + customerType +
                ", customer=" + customer +
                ", productDirection=" + productDirection +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", team=" + team +
                ", region=" + region +
                ", links=" + links +
                ", products=" + products +
                ", creator=" + creator +
                ", contracts=" + contracts +
                ", deleted=" + deleted +
                ", manager=" + manager +
                ", contragent=" + contragent +
                ", platformName='" + platformName + '\'' +
                ", platformId=" + platformId +
                ", technicalSupportValidity=" + technicalSupportValidity +
                ", projectSlas=" + projectSlas +
                '}';
    }
}
