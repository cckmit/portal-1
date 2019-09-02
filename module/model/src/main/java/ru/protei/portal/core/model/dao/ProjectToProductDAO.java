package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.ProjectToProduct;
import ru.protei.winter.jdbc.JdbcDAO;

import java.util.List;

public interface ProjectToProductDAO extends JdbcDAO<ProjectToProduct, ProjectToProduct> {
    boolean removeProductsInProject(Long projectId, List<Long> productIds);
}
