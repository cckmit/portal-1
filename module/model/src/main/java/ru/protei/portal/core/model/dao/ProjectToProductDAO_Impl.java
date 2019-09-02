package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.ProjectToProduct;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.winter.jdbc.JdbcBaseDAO;

import java.util.List;

public class ProjectToProductDAO_Impl extends JdbcBaseDAO<ProjectToProduct, ProjectToProduct> implements ProjectToProductDAO {
    @Override
    public boolean removeByKey(ProjectToProduct key) {
        return removeByCondition("project_id=" + key.getProjectId() + " and product_id=" + key.getProductId()) > 0;
    }

    @Override
    public boolean removeProductsInProject(Long projectId, List<Long> productIds) {
        return removeByCondition("project_id = ? and product_id in " + HelperFunc.makeInArg(productIds), projectId) > 0;
    }
}
