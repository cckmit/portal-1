package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.core.model.ent.Delivery.Columns.ID;

/**
 * Информация о поставке
 */

@JdbcEntity(table = "delivery")
public class Delivery extends AuditableObject {
    public static final String AUDIT_TYPE = "Delivery";
    public static final String CASE_OBJECT_TABLE = "case_object";
    public static final String CASE_OBJECT_ALIAS = "CO";

    /**
     * Идентификатор
     */
    @JdbcId(name = ID, idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    /**
     * Дата создания
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CREATED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date created;

    /**
     * Создатель
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CREATOR, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long creatorId;

    /**
     * Дата изменения
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.MODIFIED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date modified;

    /**
     * Название
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CASE_NAME, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private String name;

    /**
     * Описание
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.INFO, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private String description;

    /**
     * Проект
     */
    @JdbcColumn(name = "project_id")
    private Long projectId;

    @JdbcJoinedObject(localColumn = "project_id", remoteColumn = Project.Columns.ID)
    private Project project;

    /**
     * Контактное лицо
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.INITIATOR, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long initiatorId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE),
            @JdbcJoinPath(localColumn = CaseObject.Columns.INITIATOR, remoteColumn = "id", table = "person"),
    })
    private PersonShortView initiator;

    /**
     * Признак
     */
    @JdbcColumn(name = "attribute")
    @JdbcEnumerated(EnumType.ID)
    private En_DeliveryAttribute attribute;

    /**
     * Статус поставки
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID, mappedColumn = CaseObject.Columns.STATE,
            table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long stateId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.STATE, remoteColumn = "id", table = "case_state", sqlTableAlias = CASE_OBJECT_ALIAS),
    })
    private CaseState state;

    /**
     * Тип
     */
    @JdbcColumn(name = "type")
    @JdbcEnumerated(EnumType.ID)
    private En_DeliveryType type;

    /**
     * Дата отправки
     */
    @JdbcColumn(name = "departure_date")
    private Date departureDate;

    /**
     * Договор
     */
    @JdbcColumn(name = "contract_id")
    private Long contractId;

    @JdbcJoinedObject(localColumn = "contract_id", remoteColumn = ID)
    private Contract contract;

    /**
     * Подписчики
     */
    @JdbcManyToMany(linkTable = "case_notifier",
            localLinkColumn = "case_id", remoteLinkColumn = "person_id")
    private Set<Person> subscribers;

    /**
     * Комплекты
     */
    @JdbcOneToMany( localColumn = ID, remoteColumn = Kit.Columns.DELIVERY_ID )
    private List<Kit> kits;

    @JdbcJoinedColumn(localColumn = ID, remoteColumn = ID, mappedColumn = CaseObject.Columns.DELETED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private boolean deleted;

    public Delivery() {}

    public Delivery(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public PersonShortView getInitiator() {
        return initiator;
    }

    public void setInitiator(PersonShortView initiator) {
        this.initiator = initiator;
    }

    public En_DeliveryAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(En_DeliveryAttribute attribute) {
        this.attribute = attribute;
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public CaseState getState() {
        return state;
    }

    public void setState(CaseState state) {
        this.state = state;
    }

    public En_DeliveryType getType() {
        return type;
    }

    public void setType(En_DeliveryType type) {
        this.type = type;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Set<Person> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<Person> subscribers) {
        this.subscribers = subscribers;
    }

    public List<Kit> getKits() {
        return kits;
    }

    public void setKits(List<Kit> kits) {
        this.kits = kits;
    }

    public interface Columns {
        String ID = "id";
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Delivery that = (Delivery) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", stateId=" + stateId +
                ", state=" + state +
                ", created=" + created +
                ", modified=" + modified +
                ", projectId=" + projectId +
                ", departureDate=" + departureDate +
                ", type=" + type +
                ", initiatorId=" + initiatorId +
                ", contractId=" + contractId +
                ", contract=" + contract +
                ", attribute=" + attribute +
                ", deleted=" + deleted +
                '}';
    }
}
