package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.core.model.view.filterwidget.Filter;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity( table = "duty_log_filter" )
public class DutyLogFilter implements Filter<FilterShortView, DutyLogQuery>, Serializable {

    @JdbcId( name = "id", idInsertMode = IdInsertMode.AUTO )
    private Long id;

    @JdbcColumn( name = "name" )
    private String name;

    @JdbcColumn( name = "query", converterType = ConverterType.JSON )
    private DutyLogQuery query;

    @JdbcColumn( name = "login_id" )
    private Long loginId;

    private SelectorsParams selectorsParams;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public DutyLogQuery getQuery() {
        return query;
    }

    public void setQuery(DutyLogQuery query) {
        this.query = query;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    @Override
    public SelectorsParams getSelectorsParams() {
        return selectorsParams;
    }

    public void setSelectorsParams(SelectorsParams selectorsParams) {
        this.selectorsParams = selectorsParams;
    }

    @Override
    public FilterShortView toShortView() {
        return new FilterShortView( this.id, this.name );
    }

    @Override
    public String toString() {
        return "DutyLogFilter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", query=" + query +
                ", loginId=" + loginId +
                '}';
    }
}