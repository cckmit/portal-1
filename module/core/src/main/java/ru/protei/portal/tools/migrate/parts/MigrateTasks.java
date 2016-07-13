package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseStateMatrixDAO;
import ru.protei.portal.core.model.dao.CaseTaskDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseTask;
import ru.protei.portal.tools.migrate.tools.MigrateAction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public class MigrateTasks implements MigrateAction {

   @Autowired
   private CaseObjectDAO caseDAO;

   @Autowired
   private CaseTaskDAO taskDAO;

   @Autowired
   private CaseStateMatrixDAO stateMatrixDAO;


   @Override
   public int orderOfExec() {
      return 11;
   }

   @Override
   public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException {

      final Map<Long, Long> oldToNewStateMap = stateMatrixDAO.getOldToNewStateMap(En_CaseType.TASK);

      long lastOldDateUpdate = 0; // получаем
      //migrateDAO.confirmMigratedLastUpdate(TM_PERSON_ITEM_CODE, new Date().getTime());


      BatchProcessTask<CaseObject> t = new BatchProcessTask<CaseObject>("\"ToDoList\".Tm_Task", "nID", caseDAO.getMaxValue("caseno", Long.class, "CASE_TYPE=?", En_CaseType.TASK.getId()));
      t.setBatchSize(1000);

      List<CaseTask> tasks = new ArrayList<>();

      t.setLastUpdate(lastOldDateUpdate)
              .process(src, caseDAO, row -> {
                 CaseObject obj = new CaseObject();
                 obj.setId(null);
                 obj.setTypeId(En_CaseType.TASK.getId());
                 obj.setCreated((Date) row.get("dtCreation"));
                 obj.setCaseNumber((Long) row.get("nID"));
                 obj.setCreatorId((Long) row.get("nCreatorID"));
                 obj.setCreatorIp((String) row.get("strClientIp"));
                 obj.setCreatorInfo((String) row.get("strClient"));
                 obj.setEmails(null);
                 obj.setExtId(En_CaseType.TASK.makeGUID(obj.getCaseNumber()));
                 obj.setImpLevel(null);
//                    obj.setImpLevel(((Number)row.get("nCriticalityId")).intValue());
                 obj.setInfo((String) row.get("strDescription"));
                 obj.setInitiatorId((Long) row.get("nCreatorID"));
                 obj.setKeywords((String) row.get("strKeyWords"));
//                    obj.setLocal(row.get("lIsLocal") == null ? 1 : ((Number)row.get("lIsLocal")).intValue());
                 obj.setName((String) row.get("strName"));
                 obj.setManagerId(null);
                 obj.setModified(new Date());
                 obj.setStateId(oldToNewStateMap.get((Long) row.get("nStatusID")));

//                    if (obj.getCreatorId() == null) {
//                        obj.setCreatorId((Long)MigrateUtils.nvl(obj.getInitiatorId(),row.get("nAccepterID")));
//                    }

                 return obj;
              });

      Map<Long, Long> caseNumberToIdMapper = caseDAO.getNumberToIdMap(En_CaseType.TASK);

      Long lastTimeFact = caseDAO.getMaxValue("caseno", Long.class, "case_type=? and exists (select * from case_task t where t.case_id=case_object.id)", En_CaseType.TASK.getId());
      new BatchProcessTask<CaseTask>("\"ToDoList\".Tm_Task", "nID", lastTimeFact)
              .setLastUpdate(lastOldDateUpdate)
              .process(src, taskDAO, row -> {
                         CaseTask task = new CaseTask();

                         task.setCaseId(caseNumberToIdMapper.get((Long) row.get("nID")));
                         task.setCompleted((Date) row.get("dExpiryDate"));
                         task.setCreated((Date) row.get("dtCreation"));
                         task.setEstPersonId((Long) row.get("nExecutorId"));
                         task.setEstTime((Long) row.get("nPlannedTime"));
                         task.setRemainTime((Long) row.get("nRemainingTime"));
                         task.setStarted((Date) row.get("dtCreation"));
                         task.setTaskInfo((String) row.get("strName"));
                         task.setTimeUnit('m');
                         task.setWorkerId((Long) row.get("nExecutorId"));
                         task.setWorkTime((Long) row.get("nSpentTime"));

                         return task;
                      }
              );


   }
}
