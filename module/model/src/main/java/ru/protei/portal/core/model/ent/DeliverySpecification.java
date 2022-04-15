package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;

@JdbcEntity(table = "specification")
public class DeliverySpecification {

    /**
     * Идентификатор
     */
    @JdbcId(name = Columns.ID, idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    /**
     * Создатель
     */
    @JdbcColumn(name = Columns.CREATOR_ID)
    private Long creatorId;

    @JdbcJoinedObject(localColumn = Columns.CREATOR_ID)
    private PersonShortView creator;

    /**
     * Дата создания
     */
    @JdbcColumn(name = "created")
    private Date created;

    /**
     * Дата изменения
     */
    @JdbcColumn(name = "modified")
    private Date modified;

    /**
     * Наименование
     */
    @JdbcColumn(name = "name")
    private String name;

    /**
     *  Используемые детали
     */
    @JdbcOneToMany(table = "detail_to_specification", localColumn = "id",
            remoteColumn = "specification_id" )
    private List<DeliveryDetailToSpecification> specifications;

    /**
     * Вложенныая спецификацая
     */
    @JdbcJoinedObject(localColumn = "id", remoteColumn = "specification_id")
    private DeliveryNestedSpecification nestedSpecification;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public PersonShortView getCreator() {
        return creator;
    }

    public void setCreator(PersonShortView creator) {
        this.creator = creator;
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

    public DeliveryNestedSpecification getNestedSpecification() {
        return nestedSpecification;
    }

    public void setNestedSpecification(DeliveryNestedSpecification nestedSpecification) {
        this.nestedSpecification = nestedSpecification;
    }

    public List<DeliveryDetailToSpecification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<DeliveryDetailToSpecification> specifications) {
        this.specifications = specifications;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        DeliverySpecification that = (DeliverySpecification) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeliverySpecification{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", creator=" + creator +
                ", created=" + created +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", specifications=" + specifications +
                ", nestedSpecification=" + nestedSpecification +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String CREATOR_ID = "creator_id";
    }
}
