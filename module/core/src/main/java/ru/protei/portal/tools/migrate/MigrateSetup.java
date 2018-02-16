package ru.protei.portal.tools.migrate;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.tools.migrate.tools.MigrateAction;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateSetup {


    @Autowired
    List<MigrateAction> migrateActions;

    @PostConstruct
    private void _init () {
        Collections.sort(migrateActions, Comparator.comparingInt(MigrateAction::orderOfExec));
    }

    public List<MigrateAction> sortedList () {
        return migrateActions;
    }
}
