package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.UserRoleQuery;
import java.util.List;
import java.util.Set;

/**
 * Реализация сервиса управления ролями
 */
public class UserRoleServiceImpl implements UserRoleService {

    private static Logger log = LoggerFactory.getLogger(UserRoleServiceImpl.class);

    @Autowired
    UserRoleDAO userRoleDAO;

    @Autowired
    PolicyService policyService;

    @Override
    public CoreResponse<List<UserRole>> userRoleList( AuthToken token, UserRoleQuery query ) {

        List<UserRole> list = userRoleDAO.listByQuery(query);

        if (list == null)
            new CoreResponse<List<UserRole>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<UserRole>>().success(list);
    }

    @Override
    public CoreResponse<UserRole> getUserRole( AuthToken authToken, Long id ) {

        UserRole person = userRoleDAO.get(id);

        return person != null ? new CoreResponse<UserRole>().success(person)
                : new CoreResponse<UserRole>().error(En_ResultStatus.NOT_FOUND);
    }


    @Override
    public CoreResponse<UserRole> saveUserRole( AuthToken token, UserRole role ) {

        if (HelperFunc.isEmpty(role.getCode())) {
            return new CoreResponse<UserRole>().error(En_ResultStatus.VALIDATION_ERROR);
        }

        if ( userRoleDAO.saveOrUpdate(role)) {
            return new CoreResponse<UserRole>().success(role);
        }

        return new CoreResponse<UserRole>().error(En_ResultStatus.INTERNAL_ERROR);
    }
}
