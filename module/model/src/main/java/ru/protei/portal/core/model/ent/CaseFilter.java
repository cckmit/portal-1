package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity( table = "case_filter" )
public class CaseFilter implements Serializable {

    @JdbcId( name = "id", idInsertMode = IdInsertMode.AUTO )
    private Long id;

    @JdbcColumn( name = "name" )
    private String name;

    @JdbcColumn( name = "params", converterType = ConverterType.JSON )
    private CaseQuery params;

    @JdbcColumn( name = "login_id" )
    private Long loginId;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public CaseQuery getParams() {
        return params;
    }

    public void setParams( CaseQuery params ) {
        this.params = params;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId( Long loginId ) {
        this.loginId = loginId;
    }

    public CaseFilterShortView toShortView() {
        return new CaseFilterShortView( this.id, this.name );
    }

    @Override
    public String toString() {
        return "CaseFilter{" +
                "id=" + id +
                ", name=" + name +
                ", params=" + params +
                ", loginId=" + loginId +
                '}';
    }
}
