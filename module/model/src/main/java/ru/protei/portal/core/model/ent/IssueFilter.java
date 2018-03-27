package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.IssueFilterParams;
import ru.protei.portal.core.model.view.IssueFilterShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity( table = "issue_filter" )
public class IssueFilter implements Serializable {

    @JdbcId( name = "id", idInsertMode = IdInsertMode.AUTO )
    private Long id;

    @JdbcColumn( name = "name" )
    private String name;

    @JdbcColumn( name = "params", converterType = ConverterType.JSON )
    private IssueFilterParams params;

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

    public IssueFilterParams getParams() {
        return params;
    }

    public void setParams( IssueFilterParams params ) {
        this.params = params;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId( Long loginId ) {
        this.loginId = loginId;
    }

    @Override
    public String toString() {
        return "IssueFilter{" +
                "id=" + id +
                ", name=" + name +
                ", params=" + params +
                ", loginId=" + loginId +
                '}';
    }

    public IssueFilterShortView toShortView() {
        return new IssueFilterShortView( this.id, this.name );
    }
}
