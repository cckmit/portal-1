package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.AbsenceFilterDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.AbsenceFilterShortView;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

public class AbsenceFilterServiceImpl implements AbsenceFilterService {

    private static Logger log = LoggerFactory.getLogger( AbsenceFilterServiceImpl.class );

    @Autowired
    AbsenceFilterDAO absenceFilterDAO;
    @Autowired
    EmployeeService employeeService;

    @Override
    public Result<List<AbsenceFilterShortView>> getShortViewList(Long loginId) {
        log.debug( "getShortViewList(): loginId={}", loginId );

        List<AbsenceFilter> absenceFilters = absenceFilterDAO.getListByLoginId( loginId );

        if ( absenceFilters == null )
            return error(En_ResultStatus.GET_DATA_ERROR );

        List<AbsenceFilterShortView> result = absenceFilters.stream().map( AbsenceFilter::toShortView ).collect( Collectors.toList() );

        return ok(result );
    }

    @Override
    public Result<AbsenceFilter> getFilter(AuthToken token, Long id) {
        log.debug( "getFilter(): id={} ", id );

        AbsenceFilter filter = absenceFilterDAO.get( id );

        if (filter == null) {
            return error( En_ResultStatus.NOT_FOUND );
        }

        Result<SelectorsParams> selectorsParams = getSelectorsParams(token, filter.getQuery());

        if (selectorsParams.isError()) {
            return error( selectorsParams.getStatus() );
        }

        filter.setSelectorsParams(selectorsParams.getData());

        return  ok( filter );
    }

    @Override
    public Result<SelectorsParams> getSelectorsParams(AuthToken token, AbsenceQuery query) {
        log.debug( "getSelectorsParams(): query={} ", query );
        SelectorsParams selectorsParams = new SelectorsParams();

        List<Long> employeeIds = collectEmployeeIds( query );
        if (!isEmpty( employeeIds )) {
            EmployeeQuery employeeQuery = new EmployeeQuery();
            employeeQuery.setIds(employeeIds);

            Result<List<PersonShortView>> result = employeeService.shortViewList( employeeQuery );
            if (result.isOk()) {
                selectorsParams.setPersonShortViews(result.getData());
            } else {
                return error(result.getStatus(), "Error at getEmployeeIds" );
            }
        }

        return ok(selectorsParams);
    }

    @Override
    @Transactional
    public Result<AbsenceFilter> saveFilter(AuthToken token, AbsenceFilter filter) {

        log.debug("saveFilter(): filter={} ", filter);

        if (isNotValid(filter)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (filter.getLoginId() == null) {
            filter.setLoginId(token.getUserLoginId());
        }

        filter.setName(filter.getName().trim());

        if (!isUniqueFilter(filter.getName(), filter.getLoginId(), filter.getId())) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        if (absenceFilterDAO.saveOrUpdate(filter)) {
            return ok(filter);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    @Transactional
    public Result<Long> removeFilter(Long id) {
        log.debug( "removeFilter(): id={} ", id );

        if (!absenceFilterDAO.removeByKey(id)) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(id);
    }

    private List<Long> collectEmployeeIds(AbsenceQuery query){
        return new ArrayList<>(emptyIfNull(query.getEmployeeIds()));
    }

    private boolean isNotValid( AbsenceFilter filter ) {
        return filter == null ||
                HelperFunc.isEmpty(filter.getName()) ||
                filter.getQuery() == null;
    }

    private boolean isUniqueFilter(String name, Long loginId, Long excludeId ) {
        AbsenceFilter filter = absenceFilterDAO.checkExistsByParams( name, loginId );
        return filter == null || filter.getId().equals( excludeId );
    }
}
