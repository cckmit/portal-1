package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_MigrationEntry;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.tools.migrate.utils.MigrateAction;
import ru.protei.portal.tools.migrate.utils.MigrateUtils;

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
    private CaseStateMatrixDAO stateMatrixDAO;

    @Autowired
    private MigrationEntryDAO migrationEntryDAO;

    @Override
    public int orderOfExec() {
        return 10;
    }

    @Override
    public void migrate(Connection sourceConnection) throws SQLException {

        final Map<Long, Long> oldToNewStateMap = stateMatrixDAO.getOldToNewStateMap(En_CaseType.BUG);

        MigrateUtils.runDefaultMigration(sourceConnection, En_MigrationEntry.BUG.getCode(),"\"BugTracking\".Tm_Bug",
                migrationEntryDAO,caseDAO, row -> {
                    CaseObject obj = new CaseObject();
                    obj.setId(null);
                    obj.setType(En_CaseType.BUG);
                    obj.setCreated((Date) row.get("dtCreation"));
                    obj.setCaseNumber((Long) row.get("nID"));
                    obj.setCreatorId((Long) row.get("nSubmitterID"));
                    obj.setCreatorIp((String) row.get("strClientIp"));
                    obj.setCreatorInfo((String) row.get("strClient"));
                    obj.setEmails(null);
                    obj.setExtId(En_CaseType.BUG.makeGUID(obj.getCaseNumber()));
                    obj.setImpLevel(((Number) row.get("nCriticalityId")).intValue());
                    obj.setInfo((String) row.get("strInfo"));
                    obj.setInitiatorId((Long) row.get("nDeclarantId"));
                    obj.setKeywords((String) row.get("strKeyWord"));
                    obj.setName((String) row.get("strSubject"));
                    obj.setManagerId(obj.getCreatorId());
                    obj.setModified(new Date());
                    obj.setStateId(oldToNewStateMap.get((Long) row.get("nStatusID")));

                    if (obj.getCreatorId() == null) {
                        obj.setCreatorId((Long) MigrateUtils.nvl(obj.getInitiatorId(), row.get("nAccepterID")));
                    }

                    return obj;
        });


       /*
      Map<Long, Long> caseNumberToIdMapper = caseDAO.getNumberToIdMap(En_CaseType.BUG);

      Long lastTermID = termDAO.getMaxValue("old_id", Long.class, "case_id in (select id from case_object where case_type=?)", En_CaseType.BUG.getId());
      new BatchProcessTask<CaseTerm>("\"BugTracking\".Tm_BugDeadline", "nID", lastTermID)
              .setLastUpdate(lastOldDateUpdate)
              .process(src, termDAO, row -> {
                 CaseTerm c = new CaseTerm();
                 c.setCreated((Date) row.get("dtCreation"));
                 c.setCaseId(caseNumberToIdMapper.get((Long) row.get("nBugID")));
                 c.setCreatorId((Long) row.get("nSubmitterId"));
                 c.setEndTime((Date) row.get("dDeadline"));
                 c.setLabelText((String) row.get("strComment"));
                 c.setStageId(null);
                 c.setTermOrder(((Long) row.get("nID")).intValue());
                 c.setTermTypeId(En_CaseTermType.DEADLINE.getId());
                 c.setOldId((Long) row.get("nID"));
                 return c;
              });

      Long lastCommentID = commentDAO.getMaxValue("old_id", Long.class, "case_id in (select id from case_object where case_type=?)", En_CaseType.BUG.getId());
      System.out.println("start from comment-id: " + lastCommentID);

      Map<Long, Long> lastStateMap = new HashMap<>();
      new BatchProcessTask<CaseComment>("\"BugTracking\".Tm_BugComment", "nID", lastCommentID)
              .setLastUpdate(lastOldDateUpdate)
              .process(src, commentDAO, row -> {
                 CaseComment c = new CaseComment();
                 c.setCreated((Date) row.get("dtCreation"));
                 c.setAuthorId((Long) row.get("nSubmitterId"));
                 c.setCaseId(caseNumberToIdMapper.get((Long) row.get("nBugID")));
                 c.setCaseStateId(oldToNewStateMap.get(row.get("nStatusId")));
                 if (c.getCaseStateId() != null) {
                    lastStateMap.put(c.getCaseId(), c.getCaseStateId());
                 } else {
                    c.setCaseStateId(lastStateMap.get(c.getCaseId()));
                 }
                 c.setClientIp((String) row.get("strClientIP"));
                 c.setReplyTo(null);
                 c.setText((String) row.get("strInfo"));
                 c.setVroomId(null);
                 c.setOldId((Long) row.get("nID"));
                 return c;
              });

*/
    }
}
