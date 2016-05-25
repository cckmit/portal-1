package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseStateMatrixDAO;
import ru.protei.portal.core.model.dao.CaseTermDAO;
import ru.protei.portal.core.model.dict.En_CaseTermType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseTerm;
import ru.protei.portal.tools.migrate.tools.CaseIdMapper;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.portal.tools.migrate.tools.MigrateUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public class MigrateBugs implements MigrateAction {

    @Autowired
    private CaseObjectDAO caseDAO;

    @Autowired
    private CaseCommentDAO commentDAO;

    @Autowired
    private CaseTermDAO termDAO;

    @Autowired
    private CaseStateMatrixDAO stateMatrixDAO;


    @Override
    public int orderOfExec() {
        return 10;
    }

    @Override
    public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException {

        final Map<Long,Long> oldToNewStateMap = stateMatrixDAO.getOldToNewStateMap(En_CaseType.BUG);


        CaseIdMapper idMapper = new CaseIdMapper();

        BatchProcessTask<CaseObject> t = new BatchProcessTask<CaseObject>("\"BugTracking\".Tm_Bug", "nID");
        t.setPostProcessor(idMapper);

        t.process(src, caseDAO, row -> {
                    CaseObject obj = new CaseObject();
                    obj.setId(null);
                    obj.setTypeId(En_CaseType.BUG.getId());
                    obj.setCreated((Date)row.get("dtCreation"));
                    obj.setCaseNumber((Long)row.get("nID"));
                    obj.setCreatorId((Long)row.get("nSubmitterID"));
                    obj.setCreatorIp((String)row.get("strClientIp"));
                    obj.setCreatorInfo((String)row.get("strClient"));
                    obj.setEmails(null);
                    obj.setExtId(En_CaseType.BUG.makeGUID(obj.getCaseNumber()));
                    obj.setImpLevel(((Number)row.get("nCriticalityId")).intValue());
                    obj.setInfo((String)row.get("strInfo"));
                    obj.setInitiatorId((Long)row.get("nDeclarantId"));
                    obj.setKeywords((String)row.get("strKeyWord"));
                    obj.setLocal(row.get("lIsLocal") == null ? 1 : ((Number)row.get("lIsLocal")).intValue());
                    obj.setName((String)row.get("strSubject"));
                    obj.setManagerId(obj.getCreatorId());
                    obj.setModified(new Date());
                    obj.setStateId(oldToNewStateMap.get((Long)row.get("nStatusID")));

                    if (obj.getCreatorId() == null) {
                        obj.setCreatorId((Long)MigrateUtils.nvl(obj.getInitiatorId(),row.get("nAccepterID")));
                    }

                    return obj;
                });

        new BatchProcessTask<CaseTerm>("\"BugTracking\".Tm_BugDeadline", "nID")
                .process(src, termDAO, row -> {
                    CaseTerm c = new CaseTerm();
                    c.setCreated((Date)row.get("dtCreation"));
                    c.setCaseId(idMapper.getRealId(En_CaseType.BUG, (Long)row.get("nBugID")));
                    c.setCreatorId((Long)row.get("nSubmitterId"));
                    c.setEndTime((Date)row.get("dDeadline"));
                    c.setLabelText((String)row.get("strComment"));
                    c.setStageId(null);
                    c.setTermOrder(((Long)row.get("nID")).intValue());
                    c.setTermTypeId(En_CaseTermType.DEADLINE.getId());
                    c.setOldId((Long)row.get("nID"));
                    return c;
                });

        new BatchProcessTask<CaseComment>("\"BugTracking\".Tm_BugComment", "nID")
                .process(src, commentDAO, row -> {
                    CaseComment c = new CaseComment();
                    c.setCreated((Date)row.get("dtCreation"));
                    c.setAuthorId((Long)row.get("nSubmitterId"));
                    c.setCaseId(idMapper.getRealId(En_CaseType.BUG, (Long)row.get("nBugID")));
                    c.setCaseStateId(oldToNewStateMap.get(row.get("nStatusId")));
                    c.setClientIp((String)row.get("strClientIP"));
                    c.setReplyTo(null);
                    c.setText((String)row.get("strInfo"));
                    c.setVroomId(null);
                    c.setOldId((Long)row.get("nID"));
                    return c;
                });

    }
}
