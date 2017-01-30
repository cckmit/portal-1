package ru.protei.portal.core.model.query;

/**
 * Фильтр по оборудованию
 */
public class EquipmentQuery extends BaseQuery {

    private String name;

    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber( String number ) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public EquipmentQuery() {
    }

    public EquipmentQuery( String name, String number ) {
        super();
        this.name = name;
        this.number = number;
    }
}
