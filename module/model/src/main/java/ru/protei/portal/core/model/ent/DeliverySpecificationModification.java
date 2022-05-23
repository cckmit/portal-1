package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.winter.jdbc.annotations.*;

@JdbcEntity(table = "specification_modification")
public class DeliverySpecificationModification {

    /**
     * Идентификатор
     */
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    @JsonProperty("id")
    private Long id;

    /**
     * Идентификатор спецификации
     */
    @JdbcColumn(name = "specification_to_specification_id")
    @JsonProperty("specificationToSpecificationId")
    private Long specificationToSpecificationId;

    /**
     * Номер исполнения
     */
    @JdbcColumn(name = "number")
    @JsonProperty("number")
    private Integer number;

    /**
     *  Номер исполнения
     */
    @JdbcColumn(name = "count")
    @JsonProperty("count")
    private Integer count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSpecificationToSpecificationId() {
        return specificationToSpecificationId;
    }

    public void setSpecificationToSpecificationId(Long specificationToSpecificationId) {
        this.specificationToSpecificationId = specificationToSpecificationId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        DeliverySpecificationModification that = (DeliverySpecificationModification) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeliverySpecificationModification{" +
                "id=" + id +
                ", specificationToSpecificationId=" + specificationToSpecificationId +
                ", number=" + number +
                ", count=" + count +
                '}';
    }
}
