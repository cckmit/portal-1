package ru.protei.portal.tools.migrate.imp;

public interface ImportDataService {
    /* Regular import methods */

    /**
     * Метод непосредственно осуществляет инкрементальный импорт данных
     */
    void runIncrementalImport ();


    /* Only at manual import stage (init)
     *
     *
     * DON'T CALL IT
     *
     *
     * /

    /**
     * import all companies, persons, products, projects, company-logins
     */
    void importInitialCommonData();

    /**
     * import all crm-sessions and comments
     */
    void importInitialSupportSessions();
}
