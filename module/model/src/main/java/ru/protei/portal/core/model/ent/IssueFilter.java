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
