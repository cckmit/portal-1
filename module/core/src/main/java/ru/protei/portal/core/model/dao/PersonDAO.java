package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Person;
import ru.protei.winter.jdbc.JdbcDAO;

/**
 * Created by michael on 04.04.16.
 */
public interface PersonDAO extends JdbcDAO<Long,Person> {
}
