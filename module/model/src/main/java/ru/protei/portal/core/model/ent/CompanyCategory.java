package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EntityOptionSupport;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by turik on 13.10.16.
 */
@JdbcEntity(table = "company_category")
public class CompanyCategory implements Serializable, EntityOptionSupport {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "category_name")
    private String name;

    public static CompanyCategory fromEntityOption(EntityOption entityOption){
        if(entityOption == null)
            return null;

        CompanyCategory category = new CompanyCategory(entityOption.getId());
        category.setName(entityOption.getDisplayText());
        return category;
    }

    public CompanyCategory() {}

    public CompanyCategory (long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public EntityOption toEntityOption() {
        return new EntityOption(this.name, this.id);
    }
}
