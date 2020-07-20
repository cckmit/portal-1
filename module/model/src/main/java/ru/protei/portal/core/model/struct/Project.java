package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
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
@JdbcEntity(table = "case_object")
public class Project extends AuditableObject {

    public static final int NOT_DELETED = CaseObject.NOT_DELETED;
    public static final String AUDIT_TYPE = "Project";
    /**
     * Идентификатор записи о проекте
     */
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    /**
     * Название проекта
     */
    @JdbcColumn(name = Columns.NAME)
    private String name;

    /**
     * Описание проекта
     */
    @JdbcColumn(name = Columns.DESCRIPTION)
    private String description;

    /**
     * Текущее состояние проекта
     */
    @JdbcColumn(name = Columns.STATE)
    private Long stateId;

    /**
     * Тип заказчика
     */
    @JdbcColumn(name = Columns.CUSTOMER_TYPE)
    @JdbcEnumerated( EnumType.ID )
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
    @JdbcColumn(name = Columns.CREATED)
    private Date created;

    /**
     * Создатель проекта
     */
    @JdbcColumn(name = Project.Columns.CREATOR)
    private Long creatorId;

    @JdbcOneToMany( table = "case_member", localColumn = "id", remoteColumn = "CASE_ID" )
    private List<CaseMember> members;

    /**
     * Команда проекта
     */
    private List<PersonProjectMemberView> team;

    @JdbcJoinedObject(table = "case_location", localColumn = "id", remoteColumn = "CASE_ID" )
    private CaseLocation location;

    private EntityOption region;

    private List<CaseLink> links;

    private Set<ProductShortView> products;

    private Person creator;

    private List<EntityOption> contracts;

    @JdbcColumn(name = Project.Columns.DELETED)
    private boolean deleted;

    private EntityOption manager;

    private EntityOption contragent;

    private String platformName;

    private Long platformId;

    private Date technicalSupportValidity;

    private List<ProjectSla> projectSlas;

    @JdbcColumn(name = Project.Columns.PAUSE_DATE)
    private Long pauseDate;

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
        if (region == null && location != null) {
            region = EntityOption.fromLocation( location.getLocation() );
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

    public static Project fromCaseObject(CaseObject caseObject) {
        if (caseObject == null) {
            return null;
        }

        Project project = new Project();
        project.setId( caseObject.getId() );
        project.setName( caseObject.getName() );
        project.setCreator(caseObject.getCreator());
        project.setDescription(caseObject.getInfo());
        project.setState( En_RegionState.forId( caseObject.getStateId() ) );
        project.setDeleted(caseObject.isDeleted());
        if ( caseObject.getProduct() != null ) {
            project.setProductDirection( new EntityOption(
                caseObject.getProduct().getName(), caseObject.getProduct().getId()
            ) );
        }

        project.setCustomerType(En_CustomerType.find(caseObject.getLocal()));
        project.setCustomer(caseObject.getInitiatorCompany());

        if (caseObject.getMembers() != null) {
            List<En_DevUnitPersonRoleType> projectRoles = En_DevUnitPersonRoleType.getProjectRoles();
            project.setTeam( caseObject.getMembers().stream()
                    .filter(member -> projectRoles.contains(member.getRole()))
                    .map(member -> PersonProjectMemberView.fromFullNamePerson(member.getMember(), member.getRole()))
                    .collect(Collectors.toList()) );
        }

        project.setCreated( caseObject.getCreated() );

        List<CaseLocation> locations = caseObject.getLocations();
        if ( locations != null && !locations.isEmpty() ) {
            project.setRegion( EntityOption.fromLocation( locations.get( 0 ).getLocation() ) );
        }

        if (caseObject.getProducts() != null) {
            project.setProducts( caseObject.getProducts().stream()
                                        .map(ProductShortView::fromProduct)
                                        .collect(Collectors.toSet()) );
        }

        project.setContracts(caseObject.getContracts());

        if (caseObject.getManager() != null) {
            project.setManager(new EntityOption(caseObject.getManager().getDisplayShortName(), caseObject.getManagerId()));
        }

        if (caseObject.getInitiatorCompany() != null) {
            project.setContragent(new EntityOption(caseObject.getInitiatorCompany().getCname(), caseObject.getInitiatorCompanyId()));
        }

        if (caseObject.getPlatformId() != null){
            project.setPlatformId(caseObject.getPlatformId());
            project.setPlatformName(caseObject.getPlatformName());

        }

        project.setTechnicalSupportValidity(caseObject.getTechnicalSupportValidity());

        project.setProjectSlas(caseObject.getProjectSlas());

        project.setPauseDate( caseObject.getPauseDate() );

        return project;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    public void setPauseDate( Long pauseDateTimestamp ) {
        this.pauseDate = pauseDateTimestamp;
    }

    public Long getPauseDate() {
        return pauseDate;
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
                ", pauseDate=" + pauseDate +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String PAUSE_DATE = CaseObject.Columns.PAUSE_DATE;
        String CASE_TYPE = CaseObject.Columns.CASE_TYPE;
        String DELETED = CaseObject.Columns.DELETED;
        String DESCRIPTION = CaseObject.Columns.INFO;
        String CUSTOMER_TYPE = CaseObject.Columns.ISLOCAL;
        String CREATED = CaseObject.Columns.CREATED;
        String CREATOR = CaseObject.Columns.CREATOR;
        String STATE = CaseObject.Columns.STATE;
        String NAME = CaseObject.Columns.CASE_NAME;
    }
}
