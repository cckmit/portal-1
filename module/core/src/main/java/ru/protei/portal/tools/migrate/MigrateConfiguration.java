package ru.protei.portal.tools.migrate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.portal.tools.migrate.parts.MigrateCompaniesAction;
import ru.protei.portal.tools.migrate.parts.MigrateCrmSessions;
import ru.protei.portal.tools.migrate.parts.MigrateDevUnits;
import ru.protei.portal.tools.migrate.parts.MigratePersonAction;
import ru.protei.portal.tools.migrate.tools.MigrateAction;

/**
 * Created by michael on 01.04.16.
 */
@Configuration
public class MigrateConfiguration {

   @Bean
   public MigrateAction getCompanyMigrateAction() {
      return new MigrateCompaniesAction();
   }

   @Bean
   MigrateAction getPersonMigrateAction() {
      return new MigratePersonAction();
   }


   @Bean
   MigrateAction getCRM_SessionMigrateAction () {
       return new MigrateCrmSessions();
   }

//   @Bean
//   MigrateAction getPersonAbsenceAction() {
//      return new MigratePersonAbsenceAction();
//   }

//    @Bean
//    public MigrateAction getBugsMigrateAction () {
//        return new MigrateBugs();
//    }
//
//    @Bean
//    public MigrateAction getTaskMigrateAction () {
//        return new MigrateTasks();
//    }

    @Bean
    public MigrateAction getDevUnitMigrateAction () {
        return new MigrateDevUnits();
    }

//    @Bean
//    public MigrateAction getFreqMigrateAction () {
//        return new MigrateFreq();
//    }

   @Bean
   public MigrateSetup getSetup() {
      return new MigrateSetup();
   }
}
