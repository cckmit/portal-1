package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dto.Product;
import ru.protei.winter.jdbc.JdbcDAO;

public interface ProductDAO extends JdbcDAO<Long, Product> {
}
