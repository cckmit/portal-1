package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

/**
 * Информация о проекте в регионе
 */
@JdbcEntity(table = "project")
public class Project extends AuditableObject {

    public static final String AUDIT_TYPE = "Project";

    public static final int NOT_DELETED = CaseObject.NOT_DELETED;
    public static final String CASE_OBJECT_ALIAS = "CO";

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
     * Имя продукта
     */
    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = "product_id", remoteColumn = "id", table = "dev_unit")}, mappedColumn = "UNIT_NAME")
    private String productDirectionName;

    /**
     * id продукта
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = "product_id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long productDirectionId;

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

    @JdbcManyToMany(linkTable = "project_to_product", localLinkColumn = "project_id", remoteLinkColumn = "product_id")
    private Set<DevUnit> products;

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
            @JdbcJoinPath(localColumn = Columns.PLATFORM_ID, remoteColumn = "id", table = "platform")}, mappedColumn = "name")
    private String platformName;

    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = Columns.PLATFORM_ID, table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long platformId;

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

    public EntityOption getProductDirectionEntityOption() {
        if (productDirectionName != null && productDirectionId != null) {
            return new EntityOption(productDirectionName, productDirectionId);
        } else {
            return null;
        }
    }

    public String getProductDirectionName() {
        return productDirectionName;
    }

    public void setProductDirectionName(String productDirectionName) {
        this.productDirectionName = productDirectionName;
    }

    public Long getProductDirectionId() {
        return productDirectionId;
    }

    public void setProductDirectionId(Long productDirectionId) {
        this.productDirectionId = productDirectionId;
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
                    .map( member -> PersonProjectMemberView.fromFullNamePerson( member.getMember(), member.getRole() ) )
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

    public ProductShortView getSingleProduct() {
        return products == null ? null : getProducts().stream().map(ProductShortView::fromProduct).findAny().orElse(null);
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
                ", productDirectionName='" + productDirectionName + '\'' +
                ", productDirectionId=" + productDirectionId +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", members=" + members +
                ", locations=" + locations +
                ", products=" + products +
                ", deleted=" + deleted +
                ", creator=" + creator +
                ", managerId=" + managerId +
                ", managerName='" + managerName + '\'' +
                ", platformName='" + platformName + '\'' +
                ", platformId=" + platformId +
                ", technicalSupportValidity=" + technicalSupportValidity +
                ", workCompletionDate=" + workCompletionDate +
                ", purchaseDate=" + purchaseDate +
                ", projectSlas=" + projectSlas +
                ", pauseDate=" + pauseDate +
                ", regionName='" + regionName + '\'' +
                ", team=" + team +
                ", region=" + region +
                ", links=" + links +
                ", contracts=" + contracts +
                ", projectPlans=" + projectPlans +
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
