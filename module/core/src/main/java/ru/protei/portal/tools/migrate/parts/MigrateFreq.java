package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
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
public class MigrateFreq implements MigrateAction {

    @Autowired
    private CaseObjectDAO caseDAO;

    @Autowired
    private CaseStateMatrixDAO stateMatrixDAO;


    @Autowired
    private MigrationEntryDAO migrationEntryDAO;

    @Override
    public int orderOfExec() {
        return 12;
    }

    @Override
    public void migrate(Connection sourceConnection) throws SQLException {

        final Map<Long, Long> oldToNewStateMap = stateMatrixDAO.getOldToNewStateMap(En_CaseType.FREQ);


        MigrateUtils.runDefaultMigration(sourceConnection, "FREQ.Requirement", "\"FREQ\".Tm_Requirement",
                migrationEntryDAO, caseDAO,
                row -> {
                    CaseObject obj = new CaseObject();
                    obj.setId(null);
                    obj.setType(En_CaseType.FREQ);
                    obj.setCreated((Date) row.get("dtCreation"));
                    obj.setCaseNumber((Long) row.get("nID"));
                    obj.setCreatorId((Long) row.get("nCreatorID"));
//                    obj.setCreatorIp((String)row.get("strClientIp"));
//                    obj.setCreatorInfo((String)row.get("strClient"));
                    obj.setEmails((String) row.get("strEmails"));
                    obj.setExtId(En_CaseType.FREQ.makeGUID(obj.getCaseNumber()));

//                    obj.setImpLevel(((Number)row.get("nCriticalityId")).intValue());
                    obj.setInfo((String) row.get("strDescription"));
                    obj.setInitiatorId((Long) row.get("nDeclarantId"));
//                    obj.setKeywords((String)row.get("strKeyWord"));
//                    obj.setLocal(row.get("lIsLocal") == null ? 1 : ((Number) row.get("lIsLocal")).intValue());
                    obj.setName((String) row.get("strName"));
                    obj.setManagerId((Long) row.get("nManagerId"));
                    obj.setModified(new Date());
                    obj.setStateId(oldToNewStateMap.get(row.get("nStatusID")));


                    if (obj.getCreatorId() == null) {
                        obj.setCreatorId((Long) MigrateUtils.nvl(obj.getManagerId(), MigrateUtils.DEFAULT_CREATOR_ID));
                    }

                    if (obj.getInitiatorId() == null) {
                        obj.setInitiatorId((Long) MigrateUtils.nvl(obj.getManagerId(), obj.getCreatorId()));
                    }

                    return obj;
                });

/* temporary commented
        Map<Long, Long> caseNumberToIdMapper = caseDAO.getNumberToIdMap(En_CaseType.FREQ);

        Long lastTermID = termDAO.getMaxValue("old_id", Long.class, "case_id in (select id from case_object where case_type=?)", En_CaseType.FREQ.getEmployeeId());

        new BatchProcessTask<CaseTerm>("\"FREQ\".Tm_ReqDeadline", "nID", lastTermID)
                .setLastUpdate(lastOldDateUpdate)
                .process(src, termDAO, row -> {
                    CaseTerm c = new CaseTerm();
                    c.setCreated((Date) row.get("dtCreation"));
                    c.setCaseId(caseNumberToIdMapper.get(row.get("nRequirementID")));
                    c.setCreatorId((Long) row.get("nCreatorID"));
                    c.setEndTime((Date) row.get("dDeadline"));
                    c.setLabelText((String) row.get("strComment"));
                    c.setStageId(null);
                    c.setTermOrder(((Long) row.get("nID")).intValue());
                    c.setTermTypeId(En_CaseTermType.DEADLINE.getId());
                    c.setOldId((Long) row.get("nID"));
                    return c;
                });

        System.out.println("FREQ terms import done");

        Long lastDocID = documentDAO.getMaxValue("old_id", Long.class, "case_id in (select id from case_object where case_type=?)", En_CaseType.FREQ.getId());

        new BatchProcessTask<CaseDocument>("\"FREQ\".Tm_ReqDocument", "nID", lastDocID)
                .setLastUpdate(lastOldDateUpdate)
                .process(src, documentDAO, row -> {
                    CaseDocument cdoc = new CaseDocument();
                    cdoc.setCreated((Date) row.get("dtCreation"));
                    cdoc.setCaseId(caseNumberToIdMapper.get(row.get("nRequirementID")));
                    cdoc.setAuthorId((Long) row.get("nCreatorID"));
                    cdoc.setDocBody((String) row.get("text"));
                    cdoc.setRevision(((Number) row.get("nVersion")).intValue());
                    cdoc.setTypeId(((Number) row.get("nDocTypeID")).intValue());
                    cdoc.setOldId((Long) row.get("nID"));
                    return cdoc;
                });

        System.out.println("FREQ doc import done");

        Long lastCommentID = commentDAO.getMaxValue("old_id", Long.class, "case_id in (select id from case_object where case_type=?)", En_CaseType.FREQ.getId());
        System.out.println("start from comment-id: " + lastCommentID);

        Map<Long, Long> lastStateMap = new HashMap<>();
        new BatchProcessTask("\"FREQ\".Tm_ReqComment", "nID", lastCommentID)
                .setLastUpdate(lastOldDateUpdate)
                .process(src, commentDAO, row -> {
                    CaseComment c = new CaseComment();
                    c.setCreated((Date) row.get("dtCreation"));
                    c.setAuthorId((Long) row.get("nCreatorID"));
                    c.setCaseId(caseNumberToIdMapper.get(row.get("nRequirementID")));
                    c.setCaseStateId(oldToNewStateMap.get(row.get("nStatusId")));

                    if (c.getCaseStateId() != null) {
                        lastStateMap.put(c.getCaseId(), c.getCaseStateId());
                    } else {
                        c.setCaseStateId(lastStateMap.get(c.getCaseId()));
                    }

//                    c.setClientIp((String)row.get("strClientIP"));
                    c.setReplyTo(null);
                    c.setText((String) row.get("strComment"));
                    c.setVroomId(null);
                    c.setOldId((Long) row.get("nID"));
                    return c;
                });

        System.out.println("FREQ comments import done");

        */
    }
}
