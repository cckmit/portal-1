package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface EmployeeRegistrationDAO extends PortalBaseDAO<EmployeeRegistration> {

    SearchResult<EmployeeRegistration> getSearchResult(EmployeeRegistrationQuery query);

    List<EmployeeRegistration> getListByQuery(EmployeeRegistrationQuery query);

    List<EmployeeRegistration> getProbationExpireList( int daysToProbationEndDate );

    List<EmployeeRegistration> getAfterProbationList( int sendEmployeeFeedbackAfterProbationEndDays );

    EmployeeRegistration getByPersonId( Long personId );
}
