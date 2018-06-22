package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseStateMatrixDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseStateMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public class CaseStateMatrixDAO_Impl extends PortalBaseJdbcDAO<CaseStateMatrix> implements CaseStateMatrixDAO{

    public Map<Long,Long> getOldToNewStateMap (En_CaseType caseType) {

        Map<Long,Long> oldToNewStateMap = new HashMap<>();

        for (CaseStateMatrix me : getListByCondition("CASE_TYPE=? and OLD_ID is not null", caseType.getId())) {
            oldToNewStateMap.put(me.getOldId(), me.getCaseStateId());
        }

        return oldToNewStateMap;
    }

    @Override
    public List<En_CaseState> getStatesByCaseType(En_CaseType caseType) {
        List<CaseStateMatrix> caseStateMatrix = getListByCondition("CASE_TYPE=? ORDER BY VIEW_ORDER", caseType.getId());

        List<En_CaseState> caseStateList = new ArrayList<>(caseStateMatrix.size());
        caseStateMatrix.forEach(csm ->
            caseStateList.add(En_CaseState.getById(csm.getCaseStateId()))
        );

        return caseStateList;
    }
//
//    @Override TODO remove
//    public List<CaseState> getCaseStatesByCaseType(int caseTypeId) {
//
//        String sql = "SELECT cs.id, cs.STATE, cs.INFO, cs.usage_in_companies " +
//                "FROM portal_dev.case_state_matrix mtx " +
//                "LEFT JOIN case_state cs on mtx.CASE_STATE = cs.ID " +
//                "WHERE mtx.CASE_TYPE=? " +
//                "ORDER BY VIEW_ORDER";
//        try {
//            return jdbcTemplate.query(sql, rm, caseTypeId);
//        } catch (Exception e) {
//            return null;
//        }
//
//    }
//
//    private RowMapper<CaseState> rm = new RowMapper<CaseState>() {
//        @Override
//        public CaseState mapRow(ResultSet rs, int rowNum) throws SQLException {
//            CaseState state = new CaseState();
//
//            state.setId(rs.getLong("id"));
//            state.setInfo(rs.getString("INFO"));
//            state.setState(rs.getString("STATE"));
//            state.setUsageInCompanies(En_CaseStateUsageInCompanies.getButOrdinal(rs.getInt("usage_in_companies")));
//
//            return state;
//        }
//    };

}
