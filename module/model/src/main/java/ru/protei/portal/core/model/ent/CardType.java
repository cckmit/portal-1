package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

@JdbcEntity(table = "card_type")
public class CardType extends EntityOption {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="name")
    String name;

    @JdbcColumn(name="code")
    String code;

    @JdbcColumn(name="is_container")
    private boolean isContainer;

    @JdbcColumn(name="is_display")
    private boolean isDisplay;

    public CardType() {
    }

    public CardType(String name, String code, boolean isContainer, boolean isDisplay) {
        this.name = name;
        this.code = code;
        this.isContainer = isContainer;
        this.isDisplay = isDisplay;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public boolean isContainer() {
        return isContainer;
    }

    public boolean isDisplay() {
        return isDisplay;
    }
}
