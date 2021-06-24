package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

/**
 * Информация о проекте в регионе
 */
@JdbcEntity(table = "project")
public class Project extends AuditableObject {

    public static final String AUDIT_TYPE = "Project";

    public static final int NOT_DELETED = CaseObject.NOT_DELETED;
    public static final String CASE_OBJECT_ALIAS = "CO";
    public static final String CASE_MEMBER_ALIAS = "CM";

    /**
     * Идентификатор записи о проекте
     */
    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    /**
     * Название проекта
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = "CASE_NAME", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private String name;

    /**
     * Описание проекта
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = Columns.DESCRIPTION, table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private String description;

    /**
     * Текущее состояние проекта
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = Columns.STATE, table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long stateId;

    /**
     * Текущее состояние проекта в строковом виде
     */
    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = Columns.STATE, remoteColumn = "id", table = "case_state", sqlTableAlias = CASE_OBJECT_ALIAS),
    }, mappedColumn = "state")
    private String stateName;

    /**
     * Цвет иконки состояния проекта
     */
    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = Columns.STATE, remoteColumn = "id", table = "case_state")}, mappedColumn = "color")
    private String stateColor;

    /**
     * Тип заказчика
     */
    @JdbcColumn(name = Columns.CUSTOMER_TYPE)
    @JdbcEnumerated( value = EnumType.ID, mandatory = false )
    private En_CustomerType customerType;

    /**
     * Заказчик
     */
    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = "initiator_company", remoteColumn = "id", table = "company")})
    private Company customer;

    /**
     * Дата создания
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = Columns.CREATED, table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date created;

    /**
     * Создатель проекта
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = Columns.CREATOR, table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long creatorId;

    @JdbcOneToMany( table = "case_member", localColumn = "id", remoteColumn = "CASE_ID" )
    private List<CaseMember> members;

    @JdbcOneToMany(table = "case_location", localColumn = "id", remoteColumn = "CASE_ID" )
    private List<CaseLocation> locations;

    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = Columns.DELETED, table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private boolean deleted;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = "CREATOR", remoteColumn = "id", table = "person")})
    private Person creator;

    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = Columns.MANAGER, table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long managerId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = Columns.MANAGER, remoteColumn = "id", table = "person")}, mappedColumn = "displayShortName")
    private String managerName;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = Columns.MANAGER, remoteColumn = "id", table = "person")}, mappedColumn = "displayName")
    private String managerFullName;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = Columns.MANAGER, remoteColumn = "id", table = "person"),
            @JdbcJoinPath(localColumn = "company_id", remoteColumn = "id", table = "company")
    }, mappedColumn = "cname")
    private String managerCompanyName;

    @JdbcColumn(name = "technical_support_validity")
    private Date technicalSupportValidity;

    @JdbcColumn(name = "work_completion_date")
    private Date workCompletionDate;

    @JdbcColumn(name = "purchase_date")
    private Date purchaseDate;

    @JdbcOneToMany(table = "project_sla", localColumn = "id", remoteColumn = "project_id")
    private List<ProjectSla> projectSlas;

    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = Columns.PAUSE_DATE, table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long pauseDate;

    @JdbcManyToMany(linkTable = "plan_to_project", localLinkColumn = "project_id", remoteLinkColumn = "plan_id")
    private List<Plan> projectPlans;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "CASE_ID", table = "case_location", sqlTableAlias = "location"),
            @JdbcJoinPath(localColumn = "LOCATION_ID", remoteColumn = "id", table = "location", sqlTableAlias = "region"),
    }, mappedColumn = "name")
    private String regionName;

    @JdbcManyToMany(linkTable = "project_to_company", localLinkColumn = "project_id", remoteLinkColumn = "company_id")
    private List<Company> subcontractors;
    /**
     * Команда проекта
     */
    private List<PersonProjectMemberView> team;

    private EntityOption region;

    private List<CaseLink> links;

    private List<EntityOption> contracts;

    private Set<DevUnit> productDirections;

    private Set<DevUnit> products;

    private List<EntityOption> platforms;

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

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateColor() {
        return stateColor;
    }

    public void setStateColor(String stateColor) {
        this.stateColor = stateColor;
    }

    public List<EntityOption> getProductDirectionEntityOptionList() {
        if (isEmpty(productDirections)) {
            return null;
        }
        return productDirections.stream()
                .map(direction -> new EntityOption(direction.getName(), direction.getId()))
                .collect(Collectors.toList());
    }

    public Set<DevUnit> getProductDirections() {
        return productDirections;
    }

    public void setProductDirections(Set<DevUnit> productDirections) {
        this.productDirections = productDirections;
    }

    public Date getCreated() {
        return created;
    }

    public List<CaseLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<CaseLocation> locations) {
        this.locations = locations;
    }

    public void setCreated(Date created ) {
        this.created = created;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId( Long creatorId ) {
        this.creatorId = creatorId;
    }

    public EntityOption getRegion() {
        if (region == null && CollectionUtils.isNotEmpty(locations)) {
            region = EntityOption.fromLocation( locations.get(0).getLocation() );
        }
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

    public Long getCustomerId() {
        return customer == null ? null : customer.getId();
    }

    public Set<DevUnit> getProducts() {
        return products;
    }

    public void setProducts(Set<DevUnit> products) {
        this.products = products;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
        this.creatorId = creator == null ? null : creator.getId();
    }

    public List<PersonProjectMemberView> getTeam() {
        if (team == null && !isEmpty( members )) {
            team = CollectionUtils.stream( members )
                    .filter( member -> En_DevUnitPersonRoleType.isProjectRole( member.getRole() ) )
                    .map( member -> new PersonProjectMemberView( member.getMember(), member.getRole() ) )
                    .collect( Collectors.toList() );
        }
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

    public List<CaseMember> getMembers() {
        return members;
    }

    public void setMembers( List<CaseMember> members ) {
        this.members = members;
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

    public List<ProductShortView> getProductShortViewList() {
        return products == null ? null : toList(products, ProductShortView::fromProduct);
    }

    public List<EntityOption> getContracts() {
        return contracts;
    }

    public void setContracts(List<EntityOption> contracts) {
        this.contracts = contracts;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerFullName() {
        return managerFullName;
    }

    public String getManagerCompanyName() {
        return managerCompanyName;
    }

    public void setManagerCompanyName(String managerCompanyName) {
        this.managerCompanyName = managerCompanyName;
    }

    public List<EntityOption> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<EntityOption> platforms) {
        this.platforms = platforms;
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

    public void setWorkCompletionDate(Date workCompletionDate) {
        this.workCompletionDate = workCompletionDate;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<ProjectSla> getProjectSlas() {
        return projectSlas;
    }

    public void setProjectSlas(List<ProjectSla> projectSlas) {
        this.projectSlas = projectSlas;
    }

    public List<Plan> getProjectPlans() {
        return projectPlans;
    }

    public void setProjectPlans(List<Plan> projectPlans) {
        this.projectPlans = projectPlans;
    }

    public List<Company> getSubcontractors() {
        return subcontractors;
    }

    public void setSubcontractors(List<Company> subcontractors) {
        this.subcontractors = subcontractors;
    }

    public EntityOption toEntityOption() {
        return new EntityOption(this.getName(), this.getId());
    }

    public void setPauseDate( Long pauseDateTimestamp ) {
        this.pauseDate = pauseDateTimestamp;
    }

    public Long getPauseDate() {
        return pauseDate;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
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
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", members=" + members +
                ", locations=" + locations +
                ", deleted=" + deleted +
                ", creator=" + creator +
                ", managerId=" + managerId +
                ", managerName='" + managerName + '\'' +
                ", managerCompanyName='" + managerCompanyName + '\'' +
                ", platforms=" + platforms +
                ", technicalSupportValidity=" + technicalSupportValidity +
                ", workCompletionDate=" + workCompletionDate +
                ", purchaseDate=" + purchaseDate +
                ", projectSlas=" + projectSlas +
                ", pauseDate=" + pauseDate +
                ", projectPlans=" + projectPlans +
                ", regionName='" + regionName + '\'' +
                ", subcontractors=" + subcontractors +
                ", team=" + team +
                ", region=" + region +
                ", links=" + links +
                ", contracts=" + contracts +
                ", productDirections=" + productDirections +
                ", products=" + products +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String PAUSE_DATE = CaseObject.Columns.PAUSE_DATE;
        String CASE_TYPE = CaseObject.Columns.CASE_TYPE;
        String DELETED = CaseObject.Columns.DELETED;
        String DESCRIPTION = CaseObject.Columns.INFO;
        String CUSTOMER_TYPE = "customer_type";
        String CREATED = CaseObject.Columns.CREATED;
        String CREATOR = CaseObject.Columns.CREATOR;
        String STATE = CaseObject.Columns.STATE;
        String NAME = CaseObject.Columns.CASE_NAME;
        String MANAGER = CaseObject.Columns.MANAGER;
        String PLATFORM_ID = CaseObject.Columns.PLATFORM_ID;
        String COMPANY = CaseObject.Columns.INITIATOR_COMPANY;
    }
    public interface Fields {
        String PROJECT_PLANS = "projectPlans";
        String PROJECT_SUBCONTRACTORS = "subcontractors";
    }
}
