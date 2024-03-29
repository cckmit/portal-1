package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ProjectToProductDAO;
import ru.protei.portal.core.model.ent.ProjectToProduct;
import ru.protei.winter.jdbc.JdbcBaseDAO;

public class ProjectToProductDAO_Impl extends JdbcBaseDAO<ProjectToProduct, ProjectToProduct> implements ProjectToProductDAO {
    @Override
    public boolean removeByKey(ProjectToProduct key) {
        return removeByCondition("project_id=" + key.getProjectId() + " and product_id=" + key.getProductId()) > 0;
    }

    @Override
    public int removeAllProductsFromProject(Long projectId) {
        return removeByCondition("project_id = ?", projectId);
    }
}
