package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Objects;

@JdbcEntity(table = "common_manager")
public class CommonManager implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="product_id")
    private Long productId;

    @JdbcJoinedColumn(localColumn = "product_id", remoteColumn = "id",
            mappedColumn = "UNIT_NAME", table = "dev_unit", sqlTableAlias = "du")
    private String productName;

    @JdbcJoinedColumn(localColumn = "product_id", remoteColumn = "id",
            mappedColumn = "UNIT_STATE", table = "dev_unit", sqlTableAlias = "du")
    private int productState;

    @JdbcColumn(name="company_id")
    private Long companyId;
    
    @JdbcColumn(name="manager_id")
    private Long managerId;

    @JdbcJoinedColumn(localColumn = "manager_id", remoteColumn = "id",
            mappedColumn = "displayname", table = "person", sqlTableAlias = "p")
    private String managerName;

    public CommonManager() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductState() {
        return productState;
    }

    public void setProductState(int productState) {
        this.productState = productState;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonManager commonManager = (CommonManager) o;
        return Objects.equals(id, commonManager.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
