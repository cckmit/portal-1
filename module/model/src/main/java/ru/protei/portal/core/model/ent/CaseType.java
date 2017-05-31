package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

/**
 * Информация о человеке с ролью
 */
@JdbcEntity(table = "case_type")
public class CaseType implements Serializable{

    @JdbcId(name="id" , idInsertMode = IdInsertMode.AUTO)
    private Integer id;

    @JdbcColumn(name="CT_CODE")
    private String code;

    @JdbcColumn(name="CT_INFO")
    private String info;

    @JdbcColumn(name="NEXT_ID")
    private Long nextId;

    public CaseType() {
    }

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode( String code ) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo( String info ) {
        this.info = info;
    }

    public Long getNextId() {
        return nextId;
    }

    public void setNextId( Long nextId ) {
        this.nextId = nextId;
    }
}
