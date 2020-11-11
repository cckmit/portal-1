package ru.protei.portal.core.model.dao.impl;

public abstract class BaseSqlBuilder {

    protected Integer booleanAsNumber( Boolean isExpression ) {
        if (isExpression == null) return null;
        return isExpression ? 1 : 0;
    }
}
