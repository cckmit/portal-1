package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.helper.HelperFunc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 22.11.16.
 */
public class SqlCondition {

    public interface Builder {
        void build (StringBuilder condition, List<Object> args);
    }

    public String condition;
    public List<Object> args;

    public SqlCondition () {
        args = new ArrayList();
    }

    public SqlCondition(String condition) {
        this.condition = condition;
        args = new ArrayList();
    }

    public SqlCondition build (Builder builder) {
        StringBuilder sql = new StringBuilder();
        args.clear();

        builder.build(sql, args);
        this.condition = sql.toString();

        return this;
    }

    public SqlCondition(String condition, List<Object> args) {
        this.condition = condition;
        this.args = args;
    }

    public SqlCondition condition (String condition) {
        this.condition = condition;
        return this;
    }

    public SqlCondition add (Object arg) {
        this.args.add(arg);
        return this;
    }

    public boolean isConditionDefined () {
        return HelperFunc.isNotEmpty(this.condition);
    }

    public boolean isEmpty () {
        return HelperFunc.isEmpty(condition) && args.isEmpty();
    }
}
