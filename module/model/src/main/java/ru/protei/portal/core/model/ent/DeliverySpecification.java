package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;

public class DeliverySpecification {

    private Long id;

    /**
     * Дата создания
     */
    private Date created;

    /**
     * Создатель
     */
    private Long creatorId;

    private PersonShortView creator;

    /**
     * Дата изменения
     */
    private Date modified;

    /**
     * Вложенная спецификация
     */
    private Long nestedSpecificationId;

    /**
     * Артикул
     */
    private String partNumber;

    /**
     * Наименование
     */
    private String name;

    /**
     * Количество
     *  Исп1
     */
    private String implementationAmount1;

    /**
     * Признак
     *  Исп1
     */
    private String implementationSign1;

    // еще другие поля исполнения

    /**
     * Ответственный
     */
    private Long managerId;

    private PersonShortView manager;

    /**
     * Поставщик
     */
    private Long vendorCompanyId;

    private Company vendorCompany;

    /**
     * Метка для попадания в
     *  упрощенную спецификацию
     */
    private Boolean isSimplified;

    /**
     * Раздел для работы
     */
    private Long sectionForWork;

    /**
     * Размеры, мм
     */
    private String dimensions;

    /**
     * Вес, г
     */
    private Integer weight;

    public DeliverySpecification() {}

    public DeliverySpecification(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public PersonShortView getCreator() {
        return creator;
    }

    public void setCreator(PersonShortView creator) {
        this.creator = creator;
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

    public interface Fields {
    }

    public Long getNestedSpecificationId() {
        return nestedSpecificationId;
    }

    public void setNestedSpecificationId(Long nestedSpecificationId) {
        this.nestedSpecificationId = nestedSpecificationId;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getImplementationAmount1() {
        return implementationAmount1;
    }

    public void setImplementationAmount1(String implementationAmount1) {
        this.implementationAmount1 = implementationAmount1;
    }

    public String getImplementationSign1() {
        return implementationSign1;
    }

    public void setImplementationSign1(String implementationSign1) {
        this.implementationSign1 = implementationSign1;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public PersonShortView getManager() {
        return manager;
    }

    public void setManager(PersonShortView manager) {
        this.manager = manager;
    }

    public Long getVendorCompanyId() {
        return vendorCompanyId;
    }

    public void setVendorCompanyId(Long vendorCompanyId) {
        this.vendorCompanyId = vendorCompanyId;
    }

    public Company getVendorCompany() {
        return vendorCompany;
    }

    public void setVendorCompany(Company vendorCompany) {
        this.vendorCompany = vendorCompany;
    }

    public Boolean getSimplified() {
        return isSimplified;
    }

    public void setSimplified(Boolean simplified) {
        isSimplified = simplified;
    }

    public Long getSectionForWork() {
        return sectionForWork;
    }

    public void setSectionForWork(Long sectionForWork) {
        this.sectionForWork = sectionForWork;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
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
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", creator=" + creator +
                ", modified=" + modified +
                ", nestedSpecificationId=" + nestedSpecificationId +
                ", partNumber='" + partNumber + '\'' +
                ", name='" + name + '\'' +
                ", implementationAmount1='" + implementationAmount1 + '\'' +
                ", implementationSign1='" + implementationSign1 + '\'' +
                ", managerId=" + managerId +
                ", manager=" + manager +
                ", vendorCompanyId=" + vendorCompanyId +
                ", vendorCompany=" + vendorCompany +
                ", isSimplified=" + isSimplified +
                ", sectionForWork=" + sectionForWork +
                ", dimensions='" + dimensions + '\'' +
                ", weight=" + weight +
                '}';
    }
}
