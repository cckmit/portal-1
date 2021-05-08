package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DeliveryFilterType;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity( table = "delivery_filter" )
public class DeliveryFilter implements Serializable {

    @JdbcId( name = "id", idInsertMode = IdInsertMode.AUTO )
    private Long id;

    @JdbcColumn( name = "name" )
    private String name;

    @JdbcColumn( name = "params" )
    private String params;

    @JdbcColumn( name = "login_id" )
    private Long loginId;

    @JdbcColumn(name = "type")
    @JdbcEnumerated(EnumType.STRING)
    private En_DeliveryFilterType type;

    private SelectorsParams selectorsParams;

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

    public String getParams() {
        return params;
    }

    public void setParams( String params ) {
        this.params = params;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId( Long loginId ) {
        this.loginId = loginId;
    }

    public En_DeliveryFilterType getType() {
        return type;
    }

    public void setType( En_DeliveryFilterType type ) {
        this.type = type;
    }

    public FilterShortView toShortView() {
        return new FilterShortView( this.id, this.name );
    }

    public SelectorsParams getSelectorsParams() {
        return selectorsParams;
    }

    public void setSelectorsParams(SelectorsParams selectorsParams) {
        this.selectorsParams = selectorsParams;
    }

    @Override
    public String toString() {
        return "DeliveryFilter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", params=" + params +
                ", loginId=" + loginId +
                ", type=" + type +
                '}';
    }
}
