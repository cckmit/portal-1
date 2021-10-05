package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Objects;

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

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isContainer() {
        return isContainer;
    }

    public void setContainer(boolean container) {
        isContainer = container;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public void setDisplay(boolean display) {
        isDisplay = display;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardType cardType = (CardType) o;
        return id.equals(cardType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CardType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", isContainer=" + isContainer +
                ", isDisplay=" + isDisplay +
                '}';
    }
}
