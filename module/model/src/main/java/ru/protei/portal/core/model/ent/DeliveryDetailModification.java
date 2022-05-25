package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JdbcEntity(table = "detail_modification")
public class DeliveryDetailModification {

    /**
     * Идентификатор
     */
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    @JsonProperty("id")
    private Long id;

    /**
     *  Идентификатор детали в спецификации
     */
    @JdbcColumn(name = "detail_to_specification_id")
    @JsonProperty("detailToSpecificationId")
    private Long detailToSpecificationId;

    /**
     *  Номер исполнения
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

    public Long getDetailToSpecificationId() {
        return detailToSpecificationId;
    }

    public void setDetailToSpecificationId(Long detailToSpecificationId) {
        this.detailToSpecificationId = detailToSpecificationId;
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

        DeliveryDetailModification that = (DeliveryDetailModification) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeliveryDetailModification{" +
                "id=" + id +
                ", detailToSpecificationId=" + detailToSpecificationId +
                ", number=" + number +
                ", count=" + count +
                '}';
    }
}
