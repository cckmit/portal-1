package ru.protei.portal.config;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

public class SpringLiquibaseAdapter extends SpringLiquibase {

    @Override
    public void afterPropertiesSet() throws LiquibaseException {
        //       don't start create schema after bean created, use reloadSchema()
    }

    public void reloadSchema() throws LiquibaseException {
        super.afterPropertiesSet();
    }
}
