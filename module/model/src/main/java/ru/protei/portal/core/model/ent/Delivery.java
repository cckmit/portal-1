package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryStatus;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Set;

@JdbcEntity(table = "delivery")
public class Delivery extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "modified")
    private Date modified;

    @JdbcColumn(name = "name")
    private String name;

    @JdbcColumn(name = "description")
    private String description;

    @JdbcColumn(name = "project_id")
    private long projectId;

    @JdbcJoinedObject(localColumn = "project_id", remoteColumn = "id")
    private Project project;

    @JdbcColumn(name = "attribute")
    @JdbcEnumerated(EnumType.ID)
    private En_DeliveryAttribute attribute;

    @JdbcColumn(name = "status")
    @JdbcEnumerated(EnumType.ID)
    private En_DeliveryStatus status;

    @JdbcColumn(name = "type")
    @JdbcEnumerated(EnumType.ID)
    private En_DeliveryType type;

    @JdbcColumn(name = "delivered")
    private Date delivered;

    @JdbcManyToMany(linkTable = "delivery_subscriber", localLinkColumn = "delivery_id", remoteLinkColumn = "person_id")
    private Set<Person> subscribers;

    @JdbcColumn(name = "kit_id")
    private long kitId;

    @JdbcJoinedObject(localColumn = "kit_id", remoteColumn = "id")
    private Kit kit;

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

    public En_DeliveryAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(En_DeliveryAttribute attribute) {
        this.attribute = attribute;
    }

    public En_DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(En_DeliveryStatus status) {
        this.status = status;
    }

    public En_DeliveryType getType() {
        return type;
    }

    public void setType(En_DeliveryType type) {
        this.type = type;
    }

    public Date getDelivered() {
        return delivered;
    }

    public void setDelivered(Date delivered) {
        this.delivered = delivered;
    }

    public Set<Person> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<Person> subscribers) {
        this.subscribers = subscribers;
    }

    public long getKitId() {
        return kitId;
    }

    public void setKitId(long kitId) {
        this.kitId = kitId;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public static final String AUDIT_TYPE = "Delivery";
}
