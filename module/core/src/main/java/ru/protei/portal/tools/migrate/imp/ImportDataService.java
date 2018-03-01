package ru.protei.portal.tools.migrate.imp;

public interface ImportDataService {
    /* Regular import methods */

    /**
     * common entry-point to import all supported type of data
     * scheduled to execute for every minute
     */
    void incrementalImport();

    /**
     * incremental import of employees
     */
    void importEmployes();



    /* Only at manual import stage (init) */

    /**
     * import all companies, persons, products, projects, company-logins
     */
    void importInitialCommonData();

    /**
     * import all crm-sessions and comments
     */
    void importInitialSupportSessions();
}
