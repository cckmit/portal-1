package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseTermType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseDocument;
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
public class MigrateFreq implements MigrateAction {

    @Autowired
    private CaseObjectDAO caseDAO;

    @Autowired
    private CaseCommentDAO commentDAO;

    @Autowired
    private CaseTermDAO termDAO;

    @Autowired
    private CaseDocumentDAO documentDAO;

    @Autowired
    private CaseStateMatrixDAO stateMatrixDAO;


    @Override
    public int orderOfExec() {
        return 12;
    }

    @Override
    public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException {

        final Map<Long, Long> oldToNewStateMap = stateMatrixDAO.getOldToNewStateMap(En_CaseType.FREQ);


        CaseIdMapper idMapper = new CaseIdMapper();

        BatchProcessTask<CaseObject> t = new BatchProcessTask<CaseObject>("\"FREQ\".Tm_Requirement", "nID");
        t.setPostProcessor(idMapper);

        t.process(src, caseDAO, row -> {
            CaseObject obj = new CaseObject();
            obj.setId(null);
            obj.setTypeId(En_CaseType.FREQ.getId());
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
            obj.setLocal(row.get("lIsLocal") == null ? 1 : ((Number) row.get("lIsLocal")).intValue());
            obj.setName((String) row.get("strName"));
            obj.setManagerId((Long) row.get("nManagerId"));
            obj.setModified(new Date());
            obj.setStateId(oldToNewStateMap.get((Long) row.get("nStatusID")));


            if (obj.getCreatorId() == null) {
                obj.setCreatorId((Long) MigrateUtils.nvl(obj.getManagerId(), MigrateUtils.DEFAULT_CREATOR_ID));
            }

            if (obj.getInitiatorId() == null) {
                obj.setInitiatorId((Long) MigrateUtils.nvl(obj.getManagerId(), obj.getCreatorId()));
            }

            return obj;
        });

        System.out.println("FREQ import done");

        new BatchProcessTask<CaseTerm>("\"FREQ\".Tm_ReqDeadline", "nID")
                .process(src, termDAO, row -> {
                    CaseTerm c = new CaseTerm();
                    c.setCreated((Date) row.get("dtCreation"));
                    c.setCaseId(idMapper.getRealId(En_CaseType.FREQ, (Long) row.get("nRequirementID")));
                    c.setCreatorId((Long) row.get("nCreatorID"));
                    c.setEndTime((Date) row.get("dDeadline"));
                    c.setLabelText((String) row.get("strComment"));
                    c.setStageId(null);
                    c.setTermOrder(((Long) row.get("nID")).intValue());
                    c.setTermTypeId(En_CaseTermType.DEADLINE.getId());
                    return c;
                });

        System.out.println("FREQ terms import done");


        new BatchProcessTask<CaseDocument>("\"FREQ\".Tm_ReqDocument", "nID")
                .process(src, documentDAO, row -> {
                    CaseDocument cdoc = new CaseDocument();
                    cdoc.setCreated((Date) row.get("dtCreation"));
                    cdoc.setCaseId(idMapper.getRealId(En_CaseType.FREQ, (Long) row.get("nRequirementID")));
                    cdoc.setAuthorId((Long)row.get("nCreatorID"));
                    cdoc.setDocBody((String)row.get("text"));
                    cdoc.setRevision(((Number)row.get("nVersion")).intValue());
                    cdoc.setTypeId(((Number)row.get("nDocTypeID")).intValue());
                    return cdoc;
                });

        System.out.println("FREQ doc import done");

        new BatchProcessTask("\"FREQ\".Tm_ReqComment", "nID")
                .process(src, commentDAO, row -> {
                    CaseComment c = new CaseComment();
                    c.setCreated((Date) row.get("dtCreation"));
                    c.setAuthorId((Long) row.get("nCreatorID"));
                    c.setCaseId(idMapper.getRealId(En_CaseType.FREQ, (Long) row.get("nRequirementID")));
                    c.setCaseStateId(oldToNewStateMap.get(row.get("nStatusId")));
//                    c.setClientIp((String)row.get("strClientIP"));
                    c.setReplyTo(null);
                    c.setText((String) row.get("strComment"));
                    c.setVroomId(null);
                    return c;
                });
        System.out.println("FREQ comments import done");


    }
}
