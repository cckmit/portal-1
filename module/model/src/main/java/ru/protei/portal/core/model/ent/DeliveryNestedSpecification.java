package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import ru.protei.portal.core.model.dict.En_DeliverySpecificationCategory;
import ru.protei.winter.jdbc.annotations.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JdbcEntity(table = "specification_to_specification")
@ApiModel(value="Delivery Nested Specification")
public class DeliveryNestedSpecification {

    /**
     * Идентификатор
     */
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    @JsonProperty("id")
    private Long id;

    /**
     *  Идентификатор спецификации
     */
    @JdbcColumn(name = "specification_id")
    @JsonProperty("specificationId")
    @NotNull
    private Long specificationId;

    /**
     *  Идентификатор вложенной спецификации
     */
    @JdbcColumn(name = "child_specification_id")
    @JsonProperty("childSpecificationId")
    @NotNull
    private Long childSpecificationId;

    /**
     *  Раздел для работы
     */
    @JdbcColumn(name = "category")
    @JdbcEnumerated(value = EnumType.ID)
    @JsonProperty("category")
    @NotNull
    private En_DeliverySpecificationCategory category;

    /**
     *  Исполнения вложенной спецификации
     */
    @JdbcOneToMany(table = "specification_modification", localColumn = "id",
            remoteColumn = "specification_to_specification_id" )
    @JsonProperty("modifications")
    private List<DeliverySpecificationModification> modifications;

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

    public Long getChildSpecificationId() {
        return childSpecificationId;
    }

    public void setChildSpecificationId(Long childSpecificationId) {
        this.childSpecificationId = childSpecificationId;
    }

    public En_DeliverySpecificationCategory getCategory() {
        return category;
    }

    public void setCategory(En_DeliverySpecificationCategory category) {
        this.category = category;
    }

    public List<DeliverySpecificationModification> getModifications() {
        return modifications;
    }

    public void setModifications(List<DeliverySpecificationModification> modifications) {
        this.modifications = modifications;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        DeliveryNestedSpecification that = (DeliveryNestedSpecification) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeliveryNestedSpecification{" +
                "id=" + id +
                ", specificationId=" + specificationId +
                ", childSpecificationId=" + childSpecificationId +
                ", category=" + category +
                ", modifications=" + modifications +
                '}';
    }
}
