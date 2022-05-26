package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import ru.protei.portal.core.model.dict.En_DeliverySpecificationCategory;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JdbcEntity(table = "detail")
@ApiModel(value="Delivery Detail")
public class DeliveryDetail {

    /**
     * Идентификатор
     */
    @JdbcId(name = Columns.ID, idInsertMode = IdInsertMode.AUTO)
    @JsonProperty("id")
    private Long id;

    /**
     * Артикул детали
     */
    @JdbcColumn(name = "article")
    @JsonProperty("article")
    private String article;

    /**
     * Наименование
     */
    @JdbcColumn(name = "name")
    @JsonProperty("name")
    @NotNull
    private String name;

    /**
     * Ответственный
     */
    @JdbcColumn(name = Columns.RESPONSIBLE_ID)
    @JsonProperty("responsibleId")
    @NotNull
    private Long responsibleId;

    @JdbcJoinedObject(localColumn = Columns.RESPONSIBLE_ID, remoteColumn = "id", table = "person")
    @JsonProperty("responsible")
    private PersonShortView responsible;

    /**
     * Поставщик
     */
    @JdbcColumn(name = Columns.SUPPLIER_ID)
    @JsonProperty("supplierId")
    private Long supplierId;

    @JdbcJoinedObject(localColumn = Columns.SUPPLIER_ID, remoteColumn = "id", table = "company")
    @JsonProperty("supplier")
    private Company supplier;

    /**
     * Конфигурация
     */
    @JdbcColumn(name = "configuration")
    @JsonProperty("configuration")
    private String configuration;

    /**
     * Цвет
     */
    @JdbcColumn(name = "color")
    @JsonProperty("color")
    private String color;

    /**
     * Технологический запас
     */
    @JdbcColumn(name = "reserve")
    @JsonProperty("reserve")
    private Integer reserve;

    /**
     *  Раздел для работы
     */
    @JdbcColumn(name = "category")
    @JdbcEnumerated(value = EnumType.ID)
    @JsonProperty("category")
    @NotNull
    private En_DeliverySpecificationCategory category;

    /**
     * Метка попадания в упрощенную спецификацию
     */
    @JdbcColumn(name = "simplified")
    @JsonProperty("simplified")
    @NotNull
    private Boolean simplified;

    /**
     * Признак
     */
    @JdbcColumn(name = "attn")
    @JsonProperty("attn")
    @NotNull
    private Boolean attn;

    /**
     * Тип компонента
     */
    @JdbcColumn(name = "component_type")
    @JsonProperty("componentType")
    private String componentType;

    /**
     * Значение
     */
    @JdbcColumn(name = "value")
    @JsonProperty("value")
    private String value;

    /**
     *  Используется в спецификацях
     */
    @JdbcOneToMany(table = "detail_to_specification", localColumn = "id",
            remoteColumn = "detail_id" )
    @JsonProperty("specifications")
    private List<DeliveryDetailToSpecification> specifications;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(Long responsibleId) {
        this.responsibleId = responsibleId;
    }

    public PersonShortView getResponsible() {
        return responsible;
    }

    public void setResponsible(PersonShortView responsible) {
        this.responsible = responsible;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Company getSupplier() {
        return supplier;
    }

    public void setSupplier(Company supplier) {
        this.supplier = supplier;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getReserve() {
        return reserve;
    }

    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    public En_DeliverySpecificationCategory getCategory() {
        return category;
    }

    public void setCategory(En_DeliverySpecificationCategory category) {
        this.category = category;
    }

    public Boolean getSimplified() {
        return simplified;
    }

    public void setSimplified(Boolean simplified) {
        this.simplified = simplified;
    }

    public Boolean getAttn() {
        return attn;
    }

    public void setAttn(Boolean attn) {
        this.attn = attn;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

        DeliveryDetail that = (DeliveryDetail) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeliveryDetail{" +
                "id=" + id +
                ", article='" + article + '\'' +
                ", name='" + name + '\'' +
                ", responsibleId=" + responsibleId +
                ", responsible=" + responsible +
                ", supplierId=" + supplierId +
                ", supplier=" + supplier +
                ", configuration='" + configuration + '\'' +
                ", color='" + color + '\'' +
                ", reserve=" + reserve +
                ", category=" + category +
                ", simplified=" + simplified +
                ", attn=" + attn +
                ", componentType='" + componentType + '\'' +
                ", value='" + value + '\'' +
                ", specifications=" + specifications +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String RESPONSIBLE_ID = "responsible_id";
        String SUPPLIER_ID = "supplier_id";
    }
}

