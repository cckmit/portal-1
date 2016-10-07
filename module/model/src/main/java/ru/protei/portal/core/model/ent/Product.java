package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Date;

/**
 * @author michael
 */
@JdbcEntity(table = "Product")
public class Product implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="pname")
    private String pname;

    @JdbcColumn(name="info")
    private String info;

    @JdbcColumn(name="created")
    private Date created;

    public Product() {
    }

    public String getPname() {
        return this.pname;
    }

    public Long getId() {
        return this.id;
    }

    public String getInfo() {
        return this.info;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
