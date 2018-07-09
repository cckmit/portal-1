package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;

@JdbcEntity(table = "project_to_product")
public class ProjectToProduct {

    @JdbcColumn(name = "project_id")
    private Long projectId;

    @JdbcColumn(name = "product_id")
    private Long productId;

    public ProjectToProduct() {
    }

    public ProjectToProduct(Long projectId, Long productId) {
        this.projectId = projectId;
        this.productId = productId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
