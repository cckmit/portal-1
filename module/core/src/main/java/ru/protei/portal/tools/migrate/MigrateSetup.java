package ru.protei.portal.tools.migrate;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.tools.migrate.tools.MigrateAction;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateSetup {


    @Autowired
    List<MigrateAction> migrateActions;


    public List<MigrateAction> sortedList () {

        Collections.sort(migrateActions, new Comparator<MigrateAction>() {
            public int compare(MigrateAction o1, MigrateAction o2) {
                return o1.orderOfExec()-o2.orderOfExec();
            }
        });

        return migrateActions;
    }
}
