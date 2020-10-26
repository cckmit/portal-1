package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.DutyLogFilterDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DutyLogFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

public class DutyLogFilterServiceImpl implements DutyLogFilterService {

    private static Logger log = LoggerFactory.getLogger( DutyLogFilterServiceImpl.class );

    @Autowired
    DutyLogFilterDAO dutyLogFilterDAO;
    @Autowired
    EmployeeService employeeService;

    @Override
    public Result<List<FilterShortView>> getShortViewList(Long loginId) {
        log.debug( "getShortViewList(): loginId={}", loginId );

        List<DutyLogFilter> list = dutyLogFilterDAO.getListByLoginId( loginId );

        if ( list == null )
            return error(En_ResultStatus.GET_DATA_ERROR );

        List<FilterShortView> result = list.stream().map( DutyLogFilter::toShortView ).collect( Collectors.toList() );

        return ok(result );
    }

    @Override
    public Result<DutyLogFilter> getFilter(AuthToken token, Long id) {
        log.debug( "getFilter(): id={} ", id );

        DutyLogFilter filter = dutyLogFilterDAO.get( id );

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
    public Result<SelectorsParams> getSelectorsParams(AuthToken token, DutyLogQuery query) {
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
    public Result<DutyLogFilter> saveFilter(AuthToken token, DutyLogFilter filter) {

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

        if (dutyLogFilterDAO.saveOrUpdate(filter)) {
            return ok(filter);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    @Transactional
    public Result<Long> removeFilter(Long id) {

        log.debug( "removeFilter(): id={} ", id );

        if ( dutyLogFilterDAO.removeByKey( id ) ) {
            return ok(id);
        } else {
            return error(En_ResultStatus.NOT_FOUND);
        }
    }

    private List<Long> collectEmployeeIds(DutyLogQuery query){
        return new ArrayList<>(emptyIfNull(query.getPersonIds()));
    }

    private boolean isNotValid( DutyLogFilter filter ) {
        return filter == null ||
                HelperFunc.isEmpty(filter.getName()) ||
                filter.getQuery() == null;
    }

    private boolean isUniqueFilter(String name, Long loginId, Long excludeId ) {
        DutyLogFilter filter = dutyLogFilterDAO.checkExistsByParams( name, loginId );
        return filter == null || filter.getId().equals( excludeId );
    }
}