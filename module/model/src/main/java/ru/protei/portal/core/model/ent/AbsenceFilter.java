package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.AbsenceFilterShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity( table = "absence_filter" )
public class AbsenceFilter implements Serializable {

    @JdbcId( name = "id", idInsertMode = IdInsertMode.AUTO )
    private Long id;

    @JdbcColumn( name = "name" )
    private String name;

    @JdbcColumn( name = "query", converterType = ConverterType.JSON )
    private AbsenceQuery query;

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

    public AbsenceQuery getQuery() {
        return query;
    }

    public void setQuery(AbsenceQuery query) {
        this.query = query;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public SelectorsParams getSelectorsParams() {
        return selectorsParams;
    }

    public void setSelectorsParams(SelectorsParams selectorsParams) {
        this.selectorsParams = selectorsParams;
    }

    public AbsenceFilterShortView toShortView() {
        return new AbsenceFilterShortView( this.id, this.name );
    }

    @Override
    public String toString() {
        return "AbsenceFilter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", query=" + query +
                ", loginId=" + loginId +
                '}';
    }
}
