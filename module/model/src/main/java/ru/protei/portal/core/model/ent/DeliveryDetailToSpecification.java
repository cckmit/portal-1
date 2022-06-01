package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import ru.protei.winter.jdbc.annotations.*;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JdbcEntity(table = "detail_to_specification")
@ApiModel(value="Delivery Detail To Specification")
public class DeliveryDetailToSpecification {

    /**
     * Идентификатор
     */
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    @JsonProperty("id")
    private Long id;

    /**
     * Идентификатор спецификации
     */
    @JdbcColumn(name = "specification_id")
    @JsonProperty("specificationId")
    private Long specificationId;

    /**
     * Идентификатор детали
     */
    @JdbcColumn(name = "detail_id")
    @JsonProperty("detailId")
    @NotNull
    private Long detailId;

    /**
     * Дата изменения
     */
    @JdbcColumn(name = "modified")
    @JsonProperty("dateModified")
    private Date modified;

    /**
     * Примечание
     */
    @JdbcColumn(name = "note")
    @JsonProperty("note")
    private String note;

    /**
     * Part Reference
     */
    @JdbcColumn(name = "part_reference")
    @JsonProperty("partReference")
    private String partReference;

    /**
     *  Исполнения детали
     */
    @JdbcOneToMany(table = "detail_modification", localColumn = "id",
            remoteColumn = "detail_to_specification_id" )
    @JsonProperty("modifications")
    private List<DeliveryDetailModification> modifications;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(Long specificationId) {
        this.specificationId = specificationId;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPartReference() {
        return partReference;
    }

    public void setPartReference(String partReference) {
        this.partReference = partReference;
    }

    public List<DeliveryDetailModification> getModifications() {
        return modifications;
    }

    public void setModifications(List<DeliveryDetailModification> modifications) {
        this.modifications = modifications;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        DeliveryDetailToSpecification that = (DeliveryDetailToSpecification) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeliveryDetailToSpecification{" +
                "id=" + id +
                ", specificationId=" + specificationId +
                ", detailId=" + detailId +
                ", modified=" + modified +
                ", note='" + note + '\'' +
                ", partReference='" + partReference + '\'' +
                ", modifications=" + modifications +
                '}';
    }
}
