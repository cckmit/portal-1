package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.winter.jdbc.JdbcDAO;

import java.util.List;

public interface EmployeeRegistrationDAO extends JdbcDAO<Long, EmployeeRegistration> {

    List<EmployeeRegistration> getListByQuery(EmployeeRegistrationQuery query);

    int countByQuery(EmployeeRegistrationQuery query);

    List<EmployeeRegistration> getProbationExpireList( int daysToProbationEndDate );

    List<EmployeeRegistration> getAfterProbationList( int sendEmployeeFeedbackAfterProbationEndDays );

    EmployeeRegistration getByPerson( Long personId );
}
