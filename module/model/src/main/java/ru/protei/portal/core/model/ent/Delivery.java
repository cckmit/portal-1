package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryState;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
    @JdbcId(name = Columns.ID, idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    /**
     * Дата создания
     */
    @JdbcJoinedColumn(localColumn = Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CREATED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date created;

    /**
     * Дата изменения
     */
    @JdbcJoinedColumn(localColumn = Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.MODIFIED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date modified;

    /**
     * Название
     */
    @JdbcJoinedColumn(localColumn = Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CASE_NAME, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private String name;

    /**
     * Описание
     */
    @JdbcJoinedColumn(localColumn = Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.INFO, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private String description;

    /**
     * Идентификатор проекта
     */
    @JdbcColumn(name = "project_id")
    private long projectId;

    /**
     * Проект
     */
    @JdbcJoinedObject(localColumn = "project_id", remoteColumn = Project.Columns.ID)
    private Project project;

    /**
     * Контактное лицо
     */
    @JdbcJoinedColumn(localColumn = Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.INITIATOR, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long initiatorId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = Columns.ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE),
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
     * Признак
     */
    @JdbcJoinedColumn(localColumn = Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.STATE, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    @JdbcEnumerated(EnumType.ID)
    private En_DeliveryState state;

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
     * Подписчики
     */
    @JdbcManyToMany(linkTable = "delivery_subscriber",
            localLinkColumn = "delivery_id", remoteLinkColumn = "person_id")
    private Set<Person> subscribers;

    /**
     * Комплекты
     */
    @JdbcManyToMany(linkTable = "delivery_subscriber",
            localLinkColumn = "delivery_id", remoteLinkColumn = "person_id")
    private List<Kit> kits;

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

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
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

    public En_DeliveryState getState() {
        return state;
    }

    public void setState(En_DeliveryState state) {
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
}
