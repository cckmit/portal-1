package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.winter.jdbc.JdbcBaseDAO;

/**
 * Created by michael on 04.04.16.
 */
public class PersonDAO_Impl extends JdbcBaseDAO<Long,Person> implements PersonDAO {
}
